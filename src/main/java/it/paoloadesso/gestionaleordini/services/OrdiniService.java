package it.paoloadesso.gestionaleordini.services;

import it.paoloadesso.gestionaleordini.dto.*;
import it.paoloadesso.gestionaleordini.entities.OrdiniEntity;
import it.paoloadesso.gestionaleordini.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestionaleordini.entities.ProdottiEntity;
import it.paoloadesso.gestionaleordini.entities.TavoliEntity;
import it.paoloadesso.gestionaleordini.entities.keys.OrdiniProdottiId;
import it.paoloadesso.gestionaleordini.enums.StatoOrdine;
import it.paoloadesso.gestionaleordini.enums.StatoTavolo;
import it.paoloadesso.gestionaleordini.exceptionhandling.EntitaNonTrovataException;
import it.paoloadesso.gestionaleordini.exceptionhandling.ModificaVuotaException;
import it.paoloadesso.gestionaleordini.exceptionhandling.StatoNonValidoException;
import it.paoloadesso.gestionaleordini.mapper.OrdiniMapper;
import it.paoloadesso.gestionaleordini.repositories.OrdiniProdottiRepository;
import it.paoloadesso.gestionaleordini.repositories.OrdiniRepository;
import it.paoloadesso.gestionaleordini.repositories.ProdottiRepository;
import it.paoloadesso.gestionaleordini.repositories.TavoliRepository;
import it.paoloadesso.gestionaleordini.utils.DataLavorativaUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrdiniService {

    private static final Logger log = LoggerFactory.getLogger(OrdiniService.class);

    private final OrdiniRepository ordiniRepository;
    private final TavoliRepository tavoliRepository;
    private final ProdottiRepository prodottiRepository;
    private final OrdiniProdottiRepository ordiniProdottiRepository;

    private final OrdiniMapper ordiniMapper;

    private final DataLavorativaUtil dataLavorativaUtil;

    public OrdiniService(OrdiniRepository ordiniRepository, TavoliRepository tavoliRepository,
                         ProdottiRepository prodottiRepository, OrdiniProdottiRepository ordiniProdottiRepository,
                         DataLavorativaUtil dataLavorativaUtil, OrdiniMapper ordiniMapper) {
        this.ordiniRepository = ordiniRepository;
        this.tavoliRepository = tavoliRepository;
        this.prodottiRepository = prodottiRepository;
        this.ordiniProdottiRepository = ordiniProdottiRepository;
        this.dataLavorativaUtil = dataLavorativaUtil;
        this.ordiniMapper = ordiniMapper;
    }

    /**
     * Questo metodo crea un nuovo ordine per un tavolo.
     * Prima controllo che il tavolo esista, poi creo l'ordine base e salvo subito per avere l'ID.
     * Dopo aggiungo tutti i prodotti richiesti creando le relazioni nella tabella ponte ordini_prodotti.
     * Uso @Transactional perché se qualcosa va storto devo annullare tutto (ordine + prodotti).
     */
    @Transactional
    public OrdiniDTO creaOrdine(CreaOrdiniDTO dto) {
        log.info("Tentativo di creazione ordine per tavolo ID: {}", dto.getIdTavolo());

        // Prima cosa: controllo che il tavolo esista davvero nel database
        controlloSeIlTavoloEsiste(dto.getIdTavolo());

        // Carico l'entity completa del tavolo dal database perché mi servirà dopo
        TavoliEntity tavolo = tavoliRepository.findById(dto.getIdTavolo())
                .orElseThrow(() -> {
                    log.error("Tavolo con ID {} non trovato per creazione ordine", dto.getIdTavolo());
                    return new EntitaNonTrovataException("Tavolo", dto.getIdTavolo());
                });

        log.debug("Tavolo trovato: {} - Stato: {}", tavolo.getNumeroNomeTavolo(), tavolo.getStatoTavolo());

        // Creo l'ordine base (senza prodotti ancora) e lo salvo subito per avere l'ID generato
        OrdiniEntity ordine = new OrdiniEntity();
        ordine.setTavolo(tavolo);
        ordine.setDataOrdine(oggiLavorativo());
        ordine = ordiniRepository.save(ordine); // Ora ho l'ID generato automaticamente dal database

        log.debug("Ordine base creato con ID: {}", ordine.getIdOrdine());

        // Creo una lista per contenere tutte le relazioni ordine-prodotto
        List<OrdiniProdottiEntity> ordiniProdottiEntities = new ArrayList<>();

        // Per ogni prodotto che l'utente vuole ordinare
        for (ProdottiOrdinatiRequestDTO prodottoDto : dto.getListaProdottiOrdinati()) {
            log.debug("Aggiunta prodotto ID {} all'ordine - Quantità: {}",
                    prodottoDto.getIdProdotto(), prodottoDto.getQuantitaProdotto());

            // Controllo che il prodotto esista nel database
            ProdottiEntity prodotto = prodottiRepository.findById(prodottoDto.getIdProdotto())
                    .orElseThrow(() -> {
                        log.error("Prodotto con ID {} non trovato per l'ordine", prodottoDto.getIdProdotto());
                        return new EntitaNonTrovataException("Prodotto", prodottoDto.getIdProdotto());
                    });

            // Creo la chiave composita per la tabella ponte ordini_prodotti
            // (perché la tabella ha una chiave primaria composta da idOrdine + idProdotto)
            OrdiniProdottiId id = new OrdiniProdottiId(ordine.getIdOrdine(), prodotto.getId());

            // Creo l'entity che rappresenta la relazione tra questo ordine e questo prodotto
            OrdiniProdottiEntity ordineProdotti = new OrdiniProdottiEntity();
            ordineProdotti.setId(id);
            ordineProdotti.setOrdine(ordine);
            ordineProdotti.setProdotto(prodotto);
            ordineProdotti.setQuantitaProdotto(prodottoDto.getQuantitaProdotto());

            // Aggiungo questa relazione alla lista
            ordiniProdottiEntities.add(ordineProdotti);
        }

        // Salvo tutte le relazioni ordine-prodotto in una volta sola per essere più efficiente
        ordiniProdottiRepository.saveAll(ordiniProdottiEntities);
        log.debug("Salvate {} relazioni ordine-prodotto", ordiniProdottiEntities.size());

        // Cambio lo stato del tavolo in OCCUPATO se non lo è già
        if (tavolo.getStatoTavolo() != StatoTavolo.OCCUPATO) {
            log.debug("Cambio stato tavolo da {} a OCCUPATO", tavolo.getStatoTavolo());
            tavolo.setStatoTavolo(StatoTavolo.OCCUPATO);
            tavoliRepository.save(tavolo);
        }

        log.info("Ordine creato con successo - ID: {}, Tavolo: {}, Prodotti: {}",
                ordine.getIdOrdine(), tavolo.getNumeroNomeTavolo(), ordiniProdottiEntities.size());

        // Creo il DTO di risposta manualmente con i dati dell'ordine appena creato
        OrdiniDTO ordineDto = new OrdiniDTO();
        ordineDto.setIdOrdine(ordine.getIdOrdine());
        ordineDto.setIdTavolo(tavolo.getId());
        ordineDto.setDataOrdine(ordine.getDataOrdine());
        ordineDto.setStatoOrdine(ordine.getStatoOrdine());

        return ordineDto;
    }

    public List<OrdiniDTO> getListaTuttiOrdiniAperti() {
        log.debug("Richiesta lista tutti gli ordini aperti");
        // Cerco tutti gli ordini che NON sono chiusi
        List<OrdiniEntity> ordini = ordiniRepository.findByStatoOrdineNot(StatoOrdine.CHIUSO);
        log.info("Trovati {} ordini aperti", ordini.size());

        // Converto ogni Entity in DTO
        return ordini.stream()
                .map(ordiniMapper::ordiniEntityToDto)
                .collect(Collectors.toList());
    }

    public List<OrdiniDTO> getListaOrdiniApertiByTavolo(Long idTavolo) {
        log.debug("Richiesta ordini aperti per tavolo ID: {}", idTavolo);

        // Prima controllo che il tavolo esista
        controlloSeIlTavoloEsiste(idTavolo);

        // Cerco tutti gli ordini di questo tavolo che NON sono chiusi
        List<OrdiniEntity> ordini = ordiniRepository.findByTavoloIdAndStatoOrdineNot(idTavolo, StatoOrdine.CHIUSO);
        log.info("Trovati {} ordini aperti per tavolo ID {}", ordini.size(), idTavolo);

        // Converto ogni Entity in DTO usando il mapper e ritorno la lista
        return ordini.stream()
                .map(ordiniMapper::ordiniEntityToDto)
                .collect(Collectors.toList());
    }

    public List<OrdiniDTO> getOrdiniDiOggi() {
        LocalDate oggi = oggiLavorativo();
        log.debug("Richiesta ordini di oggi (turno lavorativo): {}", oggi);

        // Cerco ordini con data di oggi che NON sono chiusi
        List<OrdiniEntity> ordini = ordiniRepository.findByDataOrdineAndStatoOrdineNot(oggi, StatoOrdine.CHIUSO);
        log.info("Trovati {} ordini di oggi aperti", ordini.size());

        // Uso method reference per convertire Entity in DTO
        return ordini.stream().map(ordiniMapper::ordiniEntityToDto).toList();
    }

    public List<OrdiniDTO> getOrdiniOggiByTavolo(@NotNull @Positive Long idTavolo) {
        LocalDate oggi = oggiLavorativo();
        log.debug("Richiesta ordini di oggi per tavolo ID: {} (data lavorativa: {})", idTavolo, oggi);

        // Prima controllo che il tavolo esista
        controlloSeIlTavoloEsiste(idTavolo);

        // Cerco ordini di questo tavolo, di oggi, che NON sono chiusi
        List<OrdiniEntity> ordini = ordiniRepository
                .findByTavoloIdAndDataOrdineAndStatoOrdineNot(idTavolo, oggi, StatoOrdine.CHIUSO);

        log.info("Trovati {} ordini di oggi per tavolo ID {}", ordini.size(), idTavolo);

        return ordini.stream().map(ordiniMapper::ordiniEntityToDto).toList();
    }

    /**
     * Trova gli ordini con TUTTI i loro prodotti per un tavolo specifico.
     * Oltre ai dati dell'ordine restituisce anche QUALI prodotti ci sono in ogni ordine.
     */
    public List<ListaOrdiniEProdottiByTavoloResponseDTO> getDettaglioOrdineByIdTavolo(
            @NotNull @Positive Long idTavolo) {

        log.debug("Richiesta dettaglio ordini per tavolo ID: {}", idTavolo);
        controlloSeIlTavoloEsiste(idTavolo);

        List<OrdiniProdottiEntity> righe = ordiniProdottiRepository
                .findByOrdineTavoloIdAndOrdineStatoOrdineNot(idTavolo, StatoOrdine.CHIUSO);

        log.info("Trovate {} righe ordini-prodotti per tavolo ID {}", righe.size(), idTavolo);

        // Uso il metodo di aiuto per costruire la risposta
        return costruzioneDettagliOrdine(righe);
    }

    public List<ListaOrdiniEProdottiByTavoloResponseDTO> getDettaglioOrdineDiOggiByIdTavolo(
            @NotNull @Positive Long idTavolo) {

        LocalDate oggi = oggiLavorativo();
        log.debug("Richiesta dettaglio ordini di oggi per tavolo ID: {} (data: {})", idTavolo, oggi);

        controlloSeIlTavoloEsiste(idTavolo);

        List<OrdiniProdottiEntity> righe = ordiniProdottiRepository
                .findByOrdineTavoloIdAndOrdineDataOrdineAndOrdineStatoOrdineNot(idTavolo, oggi, StatoOrdine.CHIUSO);

        log.info("Trovate {} righe ordini-prodotti di oggi per tavolo ID {}", righe.size(), idTavolo);

        return costruzioneDettagliOrdine(righe);
    }

    /**
     * Questo metodo modifica un ordine esistente aggiungendo nuovi prodotti o cambiando tavolo.
     * Uso @Transactional perché faccio operazioni multiple che devono avere successo tutte insieme.
     * Se qualcosa va storto, il database torna allo stato precedente automaticamente.
     * <p>
     * Gestisco anche i "successi parziali": se alcuni prodotti vengono aggiunti ma altri danno errore,
     * salvo quelli buoni e notifico gli errori nel DTO di risposta.
     */
    @Transactional
    public RisultatoModificaOrdineDTO modificaOrdine(
            Long idOrdine, @Valid ModificaOrdineRequestDTO requestDto) {

        log.info("Tentativo di modifica ordine ID: {}", idOrdine);

        // Prima controllo: la richiesta deve contenere almeno una modifica
        if (requestDto.isEmpty()) {
            log.warn("Richiesta di modifica vuota per ordine ID: {}", idOrdine);
            throw new ModificaVuotaException();
        }

        // Controllo se l'ordine esiste nel database
        OrdiniEntity ordine = ordiniRepository.findById(idOrdine)
                .orElseThrow(() -> {
                    log.error("Ordine con ID {} non trovato per modifica", idOrdine);
                    return new EntitaNonTrovataException("Ordine", idOrdine);
                });

        // Controllo business: non posso modificare un ordine già chiuso
        if (ordine.getStatoOrdine() == StatoOrdine.CHIUSO) {
            log.warn("Tentativo di modifica ordine chiuso ID: {}", idOrdine);
            throw new StatoNonValidoException("modificare l'ordine", "chiuso");
        }

        log.debug("Ordine ID {} - Stato attuale: {}, Tavolo attuale: {}",
                idOrdine, ordine.getStatoOrdine(), ordine.getTavolo().getNumeroNomeTavolo());

        // Se l'utente vuole cambiare tavolo (questa operazione o riesce o fallisce completamente)
        if (requestDto.getNuovoIdTavolo() != null) {
            log.debug("Richiesto cambio tavolo per ordine ID {} -> nuovo tavolo ID: {}",
                    idOrdine, requestDto.getNuovoIdTavolo());

            // Prima controllo che il nuovo tavolo esista
            controlloSeIlTavoloEsiste(requestDto.getNuovoIdTavolo());

            // Carico l'entity completa del nuovo tavolo
            TavoliEntity nuovoTavolo = tavoliRepository.findById(requestDto.getNuovoIdTavolo())
                    .orElseThrow(() -> {
                        log.error("Nuovo tavolo con ID {} non trovato", requestDto.getNuovoIdTavolo());
                        return new EntitaNonTrovataException("Tavolo", requestDto.getNuovoIdTavolo());
                    });

            log.info("Cambio tavolo ordine ID {}: '{}' -> '{}'",
                    idOrdine, ordine.getTavolo().getNumeroNomeTavolo(), nuovoTavolo.getNumeroNomeTavolo());

            // Cambio il tavolo nell'ordine (in memoria per ora)
            ordine.setTavolo(nuovoTavolo);
        }

        // Variabili per tracciare successi e fallimenti nell'aggiunta prodotti
        List<String> errori = new ArrayList<>();
        int prodottiAggiunti = 0;
        int prodottiRimossi = 0;

        // Se l'utente vuole rimuovere dei prodotti
        if (requestDto.getProdottiDaRimuovere() != null && !requestDto.getProdottiDaRimuovere().isEmpty()) {
            log.debug("Tentativo rimozione di {} prodotti dall'ordine ID {}",
                    requestDto.getProdottiDaRimuovere().size(), idOrdine);

            // Per ogni prodotto da rimuovere, provo a processarlo singolarmente
            for (ProdottiDaRimuovereDTO prodotto : requestDto.getProdottiDaRimuovere()) {
                try {
                    // Provo a rimuovere questo singolo prodotto
                    boolean rimosso = processaRimozioneSingoloProdotto(idOrdine, prodotto);
                    if (rimosso) {
                        prodottiRimossi++;
                        log.debug("Prodotto ID {} rimosso con successo dall'ordine ID {}",
                                prodotto.getIdProdotto(), idOrdine);
                    }

                } catch (EntitaNonTrovataException | StatoNonValidoException e) {
                    // Se questo prodotto da errore, lo segno ma continuo con gli altri
                    String messaggioErrore = "Rimozione Prodotto ID " + prodotto.getIdProdotto() +
                            ": " + e.getMessage();
                    errori.add(messaggioErrore);
                    log.warn("Errore rimozione prodotto ID {} dall'ordine ID {}: {}",
                            prodotto.getIdProdotto(), idOrdine, e.getMessage());

                } catch (Exception e) {
                    // Se questo prodotto da errore, lo segno ma continuo con gli altri
                    String messaggioErrore = "Rimozione Prodotto ID " + prodotto.getIdProdotto() +
                            ": " + e.getMessage();
                    errori.add(messaggioErrore);
                    log.error("Errore imprevisto rimozione prodotto ID {} dall'ordine ID {}: {}",
                            prodotto.getIdProdotto(), idOrdine, e.getMessage());
                }
            }
        }

        // Se l'utente vuole aggiungere dei prodotti (qui gestisco errori parziali)
        if (requestDto.getProdottiDaAggiungere() != null && !requestDto.getProdottiDaAggiungere().isEmpty()) {
            log.debug("Tentativo aggiunta di {} prodotti all'ordine ID {}",
                    requestDto.getProdottiDaAggiungere().size(), idOrdine);

            // Per ogni prodotto da aggiungere, provo a processarlo singolarmente
            for (ProdottiOrdinatiRequestDTO prodotto : requestDto.getProdottiDaAggiungere()) {
                try {
                    // Provo ad aggiungere questo singolo prodotto
                    boolean aggiunto = processaSingoloProdotto(idOrdine, ordine, prodotto);
                    if (aggiunto) {
                        prodottiAggiunti++;
                        log.debug("Prodotto ID {} aggiunto con successo all'ordine ID {}",
                                prodotto.getIdProdotto(), idOrdine);
                    }
                } catch (EntitaNonTrovataException e) {
                    // Se questo prodotto da errore, lo segno ma continuo con gli altri
                    String messaggioErrore = "Aggiunta Prodotto ID " + prodotto.getIdProdotto() +
                            ": " + e.getMessage();
                    errori.add(messaggioErrore);
                    log.warn("Errore aggiunta prodotto ID {} all'ordine ID {}: {}",
                            prodotto.getIdProdotto(), idOrdine, e.getMessage());

                } catch (Exception e) {
                    // Se questo prodotto da errore, lo segno ma continuo con gli altri
                    String messaggioErrore = "Aggiunta Prodotto ID " + prodotto.getIdProdotto() +
                            ": " + e.getMessage();
                    errori.add(messaggioErrore);
                    log.error("Errore imprevisto aggiunta prodotto ID {} all'ordine ID {}: {}",
                            prodotto.getIdProdotto(), idOrdine, e.getMessage());
                }
            }
        }

        // Salvo sempre le modifiche dell'ordine (cambio tavolo, prodotti aggiunti con successo)
        ordiniRepository.save(ordine);

        // Ricarico tutti i prodotti dell'ordine per la risposta aggiornata
        List<OrdiniProdottiEntity> righe = ordiniProdottiRepository
                .findByOrdineIdOrdine(idOrdine);

        ListaOrdiniEProdottiByTavoloResponseDTO ordineAggiornato = costruzioneDettagliOrdine(righe).get(0);

        log.info("Modifica ordine ID {} completata - Aggiunti: {}, Rimossi: {}, Errori: {}",
                idOrdine, prodottiAggiunti, prodottiRimossi, errori.size());

        // Creo il risultato con informazioni complete su successi e fallimenti
        return creaRisultatoModifica(ordineAggiornato, prodottiAggiunti, prodottiRimossi, errori, requestDto);
    }

    @Transactional
    public RisultatoModificaStatoOrdineDTO modificaStatoOrdine(Long idOrdine, ModificaStatoOrdineRequestDTO request) {
        log.info("Tentativo modifica stato ordine ID: {} -> {}", idOrdine, request.getNuovoStato());

        // Trova l'ordine
        OrdiniEntity ordine = ordiniRepository.findById(idOrdine)
                .orElseThrow(() -> {
                    log.error("Ordine con ID {} non trovato per modifica stato", idOrdine);
                    return new EntitaNonTrovataException("Ordine", idOrdine);
                });

        StatoOrdine vecchioStato = ordine.getStatoOrdine();
        StatoOrdine nuovoStato = request.getNuovoStato();

        log.debug("Ordine ID {} - Stato attuale: {}, Stato richiesto: {}", idOrdine, vecchioStato, nuovoStato);

        // Validazione: non può modificare ordini chiusi
        if (vecchioStato == StatoOrdine.CHIUSO) {
            log.warn("Tentativo di modifica stato su ordine chiuso ID: {}", idOrdine);
            throw new StatoNonValidoException("modificare lo stato", "ordine chiuso");
        }

        // Validazione: non può impostare CHIUSO con questo endpoint
        if (nuovoStato == StatoOrdine.CHIUSO) {
            log.warn("Tentativo di chiusura ordine ID {} tramite endpoint modifica stato", idOrdine);
            throw new StatoNonValidoException("chiudere l'ordine", "usa l'endpoint dedicato");
        }

        // Se è già nello stato richiesto
        if (vecchioStato == nuovoStato) {
            log.info("Ordine ID {} già nello stato richiesto: {}", idOrdine, nuovoStato);
            return new RisultatoModificaStatoOrdineDTO(
                    idOrdine, vecchioStato, nuovoStato, true,
                    "Nessuna modifica: l'ordine è già nello stato " + nuovoStato
            );
        }

        // Modifica semplice
        ordine.setStatoOrdine(nuovoStato);
        ordiniRepository.save(ordine);

        String messaggio = String.format("Stato ordine modificato da %s a %s", vecchioStato, nuovoStato);

        log.info("Ordine {}: {} → {} {}", idOrdine, vecchioStato, nuovoStato,
                request.getNote() != null ? "(Note: " + request.getNote() + ")" : "");

        return new RisultatoModificaStatoOrdineDTO(
                idOrdine, vecchioStato, nuovoStato, true, messaggio
        );
    }

    private void controlloSeIlTavoloEsiste(Long idTavolo) {
        if (idTavolo == null || !tavoliRepository.existsById(idTavolo)) {
            log.error("Controllo esistenza tavolo fallito - ID: {}", idTavolo);
            throw new EntitaNonTrovataException("Tavolo", idTavolo);
        }
        log.debug("Controllo esistenza tavolo ID {} completato con successo", idTavolo);
    }

    /**
     * Metodo di aiuto: trasforma una lista di righe ordini_prodotti in DTO completi.
     * Con questo riesco a raggruppare le righe per ordine e per ogni ordine creare un
     * DTO con la lista dei suoi prodotti.
     */
    private List<ListaOrdiniEProdottiByTavoloResponseDTO> costruzioneDettagliOrdine(List<OrdiniProdottiEntity> righe) {
        // Raggruppo tutte le righe per ID ordine
        // Risultato: una mappa dove la chiave è l'ID ordine e il valore è la lista delle sue righe
        Map<Long, List<OrdiniProdottiEntity>> byOrdine = righe.stream()
                .collect(Collectors.groupingBy(r -> r.getOrdine().getIdOrdine()));

        log.debug("Raggruppamento righe: {} ordini distinti identificati", byOrdine.size());

        // Per ogni gruppo di righe (che rappresenta un ordine)
        return byOrdine.values().stream().map(righeOrdine -> {
            // Prendo la prima riga per avere i dati base dell'ordine
            // (tutte le righe hanno gli stessi dati ordine, cambiano solo i prodotti)
            ListaOrdiniEProdottiByTavoloResponseDTO dtoBase =
                    ordiniMapper.ordiniProdottiEntityToDto(righeOrdine.get(0));

            // Creo la lista dei prodotti scorrendo tutte le righe di questo ordine
            List<ProdottiOrdinatiResponseDTO> prodotti = righeOrdine.stream().map(e -> {
                // Per ogni riga creo un DTO prodotto
                ProdottiOrdinatiResponseDTO p = new ProdottiOrdinatiResponseDTO();
                p.setIdProdotto(e.getProdotto().getId());
                p.setQuantitaProdotto(e.getQuantitaProdotto());
                p.setStatoPagato(e.getStatoPagato());
                return p;
            }).toList();

            // Attacco la lista prodotti al DTO base dell'ordine
            dtoBase.setListaOrdineERelativiProdotti(prodotti);
            return dtoBase;
        }).toList();
    }

    /**
     * Metodo di aiuto: processa un singolo prodotto da aggiungere all'ordine.
     * Ritorna true se il prodotto è stato aggiunto/aggiornato con successo.
     * Lancia eccezioni specifiche se qualcosa va storto.
     */
    private boolean processaSingoloProdotto(Long idOrdine, OrdiniEntity ordine, ProdottiOrdinatiRequestDTO prodotto) {

        log.debug("Processamento aggiunta prodotto ID {} all'ordine ID {}",
                prodotto.getIdProdotto(), idOrdine);

        // Controllo che il prodotto esista nel database
        ProdottiEntity prodottoEntity = prodottiRepository.findById(prodotto.getIdProdotto())
                .orElseThrow(() -> {
                    log.error("Prodotto con ID {} non trovato nel menu", prodotto.getIdProdotto());
                    return new EntitaNonTrovataException("Prodotto", prodotto.getIdProdotto());
                });

        // Creo la chiave per controllare se il prodotto è già nell'ordine
        OrdiniProdottiId chiave = new OrdiniProdottiId(idOrdine, prodotto.getIdProdotto());

        // Controllo se il prodotto è già presente nell'ordine
        if (ordiniProdottiRepository.existsById(chiave)) {
            // CASO 1: Prodotto già presente - aggiorno la quantità sommando
            OrdiniProdottiEntity ordineEsistente = ordiniProdottiRepository.findById(chiave).get();
            Integer quantitaEsistente = ordineEsistente.getQuantitaProdotto();
            Integer quantitaDaAggiungere = prodotto.getQuantitaProdotto();
            Integer quantitaModificataFinale = quantitaEsistente + quantitaDaAggiungere;

            log.debug("Prodotto ID {} già presente - Quantità: {} + {} = {}",
                    prodotto.getIdProdotto(), quantitaEsistente, quantitaDaAggiungere, quantitaModificataFinale);

            ordineEsistente.setQuantitaProdotto(quantitaModificataFinale);
            ordiniProdottiRepository.save(ordineEsistente);

        } else {
            // CASO 2: Prodotto nuovo - creo una nuova relazione ordine-prodotto
            log.debug("Nuovo prodotto ID {} aggiunto all'ordine ID {} - Quantità: {}",
                    prodotto.getIdProdotto(), idOrdine, prodotto.getQuantitaProdotto());

            OrdiniProdottiEntity nuovaRelazione = new OrdiniProdottiEntity();
            nuovaRelazione.setId(chiave);
            nuovaRelazione.setOrdine(ordine);
            nuovaRelazione.setProdotto(prodottoEntity);
            nuovaRelazione.setQuantitaProdotto(prodotto.getQuantitaProdotto());

            ordiniProdottiRepository.save(nuovaRelazione);
        }

        return true;
    }

    /**
     * Metodo di aiuto: processa la rimozione di una quantità specifica di un prodotto dall'ordine.
     * Ritorna true se il prodotto è stato rimosso/aggiornato con successo.
     * Lancia eccezioni specifiche se qualcosa va storto.
     */
    private boolean processaRimozioneSingoloProdotto(Long idOrdine, ProdottiDaRimuovereDTO prodotto) {

        log.debug("Processamento rimozione prodotto ID {} dall'ordine ID {} - Quantità: {}",
                prodotto.getIdProdotto(), idOrdine, prodotto.getQuantitaDaRimuovere());

        // Creo la chiave per trovare il prodotto nell'ordine
        OrdiniProdottiId chiave = new OrdiniProdottiId(idOrdine, prodotto.getIdProdotto());

        // Controllo se il prodotto è presente nell'ordine
        OrdiniProdottiEntity ordineEsistente = ordiniProdottiRepository.findById(chiave)
                .orElseThrow(() -> {
                    log.warn("Prodotto ID {} non presente nell'ordine ID {} per rimozione",
                            prodotto.getIdProdotto(), idOrdine);
                    return new EntitaNonTrovataException(
                            "Prodotto con ID " + prodotto.getIdProdotto() + " non presente nell'ordine");
                });

        int quantitaAttuale = ordineEsistente.getQuantitaProdotto();
        int quantitaDaRimuovere = prodotto.getQuantitaDaRimuovere();

        // Controllo che non stia provando a rimuovere più di quello che c'è
        if (quantitaDaRimuovere > quantitaAttuale) {
            log.warn("Tentativo rimozione quantità eccessiva prodotto ID {} - Attuale: {}, Da rimuovere: {}",
                    prodotto.getIdProdotto(), quantitaAttuale, quantitaDaRimuovere);
            throw new StatoNonValidoException(
                    "rimuovere " + quantitaDaRimuovere + " unità",
                    "disponibili solo " + quantitaAttuale + " unità"
            );
        }

        int quantitaFinale = quantitaAttuale - quantitaDaRimuovere;

        if (quantitaFinale == 0) {
            // CASO 1: Rimuovo tutto - cancello la riga
            log.debug("Rimozione completa prodotto ID {} dall'ordine ID {}", prodotto.getIdProdotto(), idOrdine);
            ordiniProdottiRepository.delete(ordineEsistente);
        } else {
            // CASO 2: Rimozione parziale - aggiorno la quantità
            log.debug("Rimozione parziale prodotto ID {} dall'ordine ID {} - Quantità finale: {}",
                    prodotto.getIdProdotto(), idOrdine, quantitaFinale);
            ordineEsistente.setQuantitaProdotto(quantitaFinale);
            ordiniProdottiRepository.save(ordineEsistente);
        }

        return true;
    }

    /**
     * Metodo di aiuto: crea il DTO risultato con le informazioni complete
     * su cosa è andato bene e cosa è andato storto nella modifica.
     */
    private RisultatoModificaOrdineDTO creaRisultatoModifica(
            ListaOrdiniEProdottiByTavoloResponseDTO ordine,
            int prodottiAggiunti,
            int prodottiRimossi,
            List<String> errori,
            ModificaOrdineRequestDTO requestDto) {

        boolean operazioneCompleta = errori.isEmpty();
        String messaggio = costruisciMessaggio(requestDto, prodottiAggiunti, prodottiRimossi, errori, operazioneCompleta);

        return new RisultatoModificaOrdineDTO(ordine, prodottiAggiunti, prodottiRimossi, errori, operazioneCompleta, messaggio);
    }

    /**
     * Costruisce il messaggio dinamico in base alle operazioni effettuate
     */
    private String costruisciMessaggio(ModificaOrdineRequestDTO requestDto, int prodottiAggiunti,
                                       int prodottiRimossi, List<String> errori, boolean operazioneCompleta) {

        if (operazioneCompleta) {
            List<String> operazioni = new ArrayList<>();

            // Tavolo cambiato
            if (requestDto.getNuovoIdTavolo() != null) {
                operazioni.add("Tavolo cambiato");
            }

            // Prodotti aggiunti
            if (prodottiAggiunti > 0) {
                String msg = (prodottiAggiunti == 1) ? "1 prodotto aggiunto" : prodottiAggiunti + " prodotti aggiunti";
                operazioni.add(msg);
            }

            // Prodotti rimossi
            if (prodottiRimossi > 0) {
                String msg = (prodottiRimossi == 1) ? "1 prodotto rimosso" : prodottiRimossi + " prodotti rimossi";
                operazioni.add(msg);
            }

            return String.join(" e ", operazioni) + " con successo";

        } else {
            // Operazioni parziali con errori
            List<String> successi = new ArrayList<>();

            if (prodottiAggiunti > 0) {
                String msg = (prodottiAggiunti == 1) ? "1 prodotto aggiunto" : prodottiAggiunti + " prodotti aggiunti";
                successi.add(msg);
            }

            if (prodottiRimossi > 0) {
                String msg = (prodottiRimossi == 1) ? "1 prodotto rimosso" : prodottiRimossi + " prodotti rimossi";
                successi.add(msg);
            }

            String erroriMsg = (errori.size() == 1) ? "1 con errore" : errori.size() + " con errori";

            if (successi.isEmpty()) {
                return "Nessuna operazione completata a causa " + ((errori.size() == 1) ? "dell'errore" : "degli errori");
            } else {
                return String.join(" e ", successi) + ", " + erroriMsg;
            }
        }
    }

    private LocalDate oggiLavorativo() {
        return dataLavorativaUtil.getDataLavorativa();
    }
}
