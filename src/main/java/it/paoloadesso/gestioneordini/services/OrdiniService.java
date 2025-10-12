package it.paoloadesso.gestioneordini.services;

import it.paoloadesso.gestioneordini.dto.*;
import it.paoloadesso.gestioneordini.entities.OrdiniEntity;
import it.paoloadesso.gestioneordini.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestioneordini.entities.ProdottiEntity;
import it.paoloadesso.gestioneordini.entities.TavoliEntity;
import it.paoloadesso.gestioneordini.entities.keys.OrdiniProdottiId;
import it.paoloadesso.gestioneordini.enums.StatoOrdine;
import it.paoloadesso.gestioneordini.mapper.OrdiniMapper;
import it.paoloadesso.gestioneordini.repositories.OrdiniProdottiRepository;
import it.paoloadesso.gestioneordini.repositories.OrdiniRepository;
import it.paoloadesso.gestioneordini.repositories.ProdottiRepository;
import it.paoloadesso.gestioneordini.repositories.TavoliRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrdiniService {

    private final OrdiniRepository ordiniRepository;
    private final TavoliRepository tavoliRepository;
    private final ProdottiRepository prodottiRepository;
    private final OrdiniProdottiRepository ordiniProdottiRepository;

    private final OrdiniMapper ordiniMapper;

    public OrdiniService(OrdiniRepository ordiniRepository, TavoliRepository tavoliRepository, ProdottiRepository prodottiRepository, OrdiniProdottiRepository ordiniProdottiRepository, OrdiniMapper ordiniMapper) {
        this.ordiniRepository = ordiniRepository;
        this.tavoliRepository = tavoliRepository;
        this.prodottiRepository = prodottiRepository;
        this.ordiniProdottiRepository = ordiniProdottiRepository;
        this.ordiniMapper = ordiniMapper;
    }

    @Transactional
    public OrdiniDto creaOrdine(CreaOrdiniDto dto) {
        // Prima cosa: controllo che il tavolo esista davvero
        controlloSeIlTavoloEsiste(dto.getIdTavolo());

        // Carico l'entity completa del tavolo dal database
        TavoliEntity tavolo = tavoliRepository.findById(dto.getIdTavolo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tavolo non trovato"));

        // Creo l'ordine base (senza prodotti ancora) e lo salvo per avere l'ID
        OrdiniEntity ordine = new OrdiniEntity();
        ordine.setTavolo(tavolo);
        ordine = ordiniRepository.save(ordine); // Ora ho l'ID generato dal database

        // Creo la lista per contenere tutte le relazioni ordine-prodotto
        List<OrdiniProdottiEntity> ordiniProdottiEntities = new ArrayList<>();

        // Per ogni prodotto che l'utente vuole ordinare
        for (ProdottiOrdinatiRequestDto prodottoDto : dto.getListaProdottiOrdinati()) {
            // Controllo che il prodotto esista nel database
            ProdottiEntity prodotto = prodottiRepository.findById(prodottoDto.getIdProdotto())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prodotto con ID " + prodottoDto.getIdProdotto() + " non trovato"));

            // Creo la chiave composita per la tabella ponte ordini_prodotti
            OrdiniProdottiId id = new OrdiniProdottiId(ordine.getIdOrdine(), prodotto.getId());

            // Creo l'entity che rappresenta la relazione ordine-prodotto
            OrdiniProdottiEntity ordineProdotti = new OrdiniProdottiEntity();
            ordineProdotti.setId(id);
            ordineProdotti.setOrdine(ordine);
            ordineProdotti.setProdotto(prodotto);
            ordineProdotti.setQuantitaProdotto(prodottoDto.getQuantitaProdotto());

            // Aggiungo questa relazione alla lista
            ordiniProdottiEntities.add(ordineProdotti);
        }

        // Salvo tutte le relazioni ordine-prodotto in una volta sola
        ordiniProdottiRepository.saveAll(ordiniProdottiEntities);

        // Alternativa usando mapStruct e non manuale:
        // return ordiniMapper.ordiniEntityToDto(ordine);

        // Creo il DTO di risposta manualmente con i dati dell'ordine
        OrdiniDto ordineDto = new OrdiniDto();
        ordineDto.setIdOrdine(ordine.getIdOrdine());
        ordineDto.setIdTavolo(tavolo.getId());
        ordineDto.setDataOrdine(ordine.getDataOrdine());
        ordineDto.setStatoOrdine(ordine.getStatoOrdine());

        return ordineDto;
    }

    public List<OrdiniDto> getListaTuttiOrdiniAperti() {
        // Cerco tutti gli ordini che NON sono chiusi
        List<OrdiniEntity> ordini = ordiniRepository.findByStatoOrdineNot(StatoOrdine.CHIUSO);
        // Converto ogni Entity in DTO
        return ordini.stream()
                .map(el -> ordiniMapper.ordiniEntityToDto(el))
                .collect(Collectors.toList());
    }

    public List<OrdiniDto> getListaOrdiniApertiByTavolo(Long idTavolo) {
        // Prima controllo che il tavolo esista
        controlloSeIlTavoloEsiste(idTavolo);

        // Cerco tutti gli ordini di questo tavolo che NON sono chiusi
        List<OrdiniEntity> ordini = ordiniRepository.findByTavoloIdAndStatoOrdineNot(idTavolo, StatoOrdine.CHIUSO);

        // Converto ogni Entity in DTO usando il mapper e ritorno la lista
        return ordini.stream()
                .map(el -> ordiniMapper.ordiniEntityToDto(el))
                .collect(Collectors.toList());
    }

    public List<OrdiniDto> getOrdiniDiOggi() {
        // Cerco ordini con data di oggi che NON sono chiusi
        List<OrdiniEntity> ordini = ordiniRepository.findByDataOrdineAndStatoOrdineNot(LocalDate.now(), StatoOrdine.CHIUSO);
        // Uso method reference per convertire Entity in DTO
        return ordini.stream().map(ordiniMapper::ordiniEntityToDto).toList();
    }

    public List<OrdiniDto> getOrdiniOggiByTavolo(@NotNull @Positive Long idTavolo) {
        // Prima controllo che il tavolo esista
        controlloSeIlTavoloEsiste(idTavolo);
        // Cerco ordini di questo tavolo, di oggi, che NON sono chiusi
        List<OrdiniEntity> ordini = ordiniRepository
                .findByTavoloIdAndDataOrdineAndStatoOrdineNot(idTavolo, LocalDate.now(), StatoOrdine.CHIUSO);
        return ordini.stream().map(ordiniMapper::ordiniEntityToDto).toList();
    }

    /**
     * Trova gli ordini con TUTTI i loro prodotti per un tavolo specifico.
     * Questo è più complesso del metodo sopra perché oltre ai dati dell'ordine
     * voglio anche sapere QUALI prodotti ci sono in ogni ordine.
     */
    public List<ListaOrdiniEProdottiByTavoloResponseDto> getDettaglioOrdineByIdTavolo(
            @NotNull @Positive Long idTavolo) {

        // Controllo che il tavolo esista
        controlloSeIlTavoloEsiste(idTavolo);

        // Cerco tutte le righe della tabella ponte ordini_prodotti
        // per questo tavolo, ma solo per ordini NON chiusi
        List<OrdiniProdottiEntity> righe = ordiniProdottiRepository
                .findByOrdineTavoloIdAndOrdineStatoOrdineNot(idTavolo, StatoOrdine.CHIUSO);

        // Uso il metodo di aiuto per costruire la risposta
        return costruzioneDettagliOrdine(righe);
    }

    public List<ListaOrdiniEProdottiByTavoloResponseDto> getDettaglioOrdineDiOggiByIdTavolo(
            @NotNull @Positive Long idTavolo) {

        // Controllo esistenza tavolo
        controlloSeIlTavoloEsiste(idTavolo);

        // Cerco righe ordini_prodotti per questo tavolo, di oggi, non chiusi
        List<OrdiniProdottiEntity> righe = ordiniProdottiRepository
                .findByOrdineTavoloIdAndOrdineDataOrdineAndOrdineStatoOrdineNot(idTavolo, LocalDate.now() , StatoOrdine.CHIUSO);

        return costruzioneDettagliOrdine(righe);
    }

    @Transactional
    public RisultatoModificaOrdineDto modificaOrdine(
            Long idOrdine, @Valid ModificaOrdineRequestDto requestDto) {
        // Prima controllo: la richiesta deve contenere almeno una modifica
        if (requestDto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La richiesta di modifica è vuota.");
        }

        // Controllo se l'ordine esiste nel database
        OrdiniEntity ordine = ordiniRepository.findById(idOrdine)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Ordine con ID «" + idOrdine + "» non trovato."));

        // Controllo business: non posso modificare un ordine già chiuso
        if (ordine.getStatoOrdine() == StatoOrdine.CHIUSO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "L'ordine è chiuso, non è possibile modificarlo.");
        }

        // Se l'utente vuole cambiare tavolo (questa operazione o riesce o fallisce completamente)
        if (requestDto.getNuovoIdTavolo() != null) {
            // Prima controllo che il nuovo tavolo esista
            controlloSeIlTavoloEsiste(requestDto.getNuovoIdTavolo());

            // Carico l'entity completa del nuovo tavolo
            TavoliEntity nuovoTavolo = tavoliRepository.findById(requestDto.getNuovoIdTavolo())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Tavolo con ID «" + requestDto.getNuovoIdTavolo() + "» non trovato."));

            // Cambio il tavolo nell'ordine (in memoria per ora)
            ordine.setTavolo(nuovoTavolo);
        }

        // Variabili per tracciare successi e fallimenti nell'aggiunta prodotti
        List<String> errori = new ArrayList<>();
        int prodottiAggiunti = 0;
        int prodottiRimossi = 0;

        // Se l'utente vuole rimuovere dei prodotti
        if (requestDto.getProdottiDaRimuovere() != null && !requestDto.getProdottiDaRimuovere().isEmpty()) {

            // Per ogni prodotto da rimuovere, provo a processarlo singolarmente
            for (ProdottiDaRimuovereDto prodotto : requestDto.getProdottiDaRimuovere()) {
                try {
                    // Provo a rimuovere questo singolo prodotto
                    boolean rimosso = processaRimozioneSingoloProdotto(idOrdine, prodotto);
                    if (rimosso) {
                        prodottiRimossi++;
                    }

                } catch (Exception e) {
                    // Se questo prodotto da errore, lo segno ma continuo con gli altri
                    String messaggioErrore = "Rimozione Prodotto ID " + prodotto.getIdProdotto() + ": " + e.getMessage();
                    errori.add(messaggioErrore);
                }
            }
        }

        // Se l'utente vuole aggiungere dei prodotti (qui gestisco errori parziali)
        if (requestDto.getProdottiDaAggiungere() != null && !requestDto.getProdottiDaAggiungere().isEmpty()) {

            // Per ogni prodotto da aggiungere, provo a processarlo singolarmente
            for (ProdottiOrdinatiRequestDto prodotto : requestDto.getProdottiDaAggiungere()) {
                try {
                    // Provo ad aggiungere questo singolo prodotto
                    boolean aggiunto = processaSingoloProdotto(idOrdine, ordine, prodotto);
                    if (aggiunto) {
                        prodottiAggiunti++;
                    }
                } catch (Exception e) {
                    // Se questo prodotto da errore, lo segno ma continuo con gli altri
                    String messaggioErrore = "Prodotto ID " + prodotto.getIdProdotto() + ": " + e.getMessage();
                    errori.add(messaggioErrore);
                }
            }
        }

        // Salvo sempre le modifiche dell'ordine (cambio tavolo, prodotti aggiunti con successo)
        ordiniRepository.save(ordine);

        // Ricarico tutti i prodotti dell'ordine per la risposta aggiornata
        List<OrdiniProdottiEntity> righe = ordiniProdottiRepository
                .findByOrdineIdOrdine(idOrdine);

        ListaOrdiniEProdottiByTavoloResponseDto ordineAggiornato = costruzioneDettagliOrdine(righe).get(0);

        // Creo il risultato con informazioni complete su successi e fallimenti
        return creaRisultatoModifica(ordineAggiornato, prodottiAggiunti, prodottiRimossi, errori, requestDto);
    }

    private void controlloSeIlTavoloEsiste(Long idTavolo) {
        if (idTavolo == null || !tavoliRepository.existsById(idTavolo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tavolo non trovato");
        }
    }

    /**
     * Metodo di aiuto: trasforma una lista di righe ordini_prodotti in DTO completi.
     * Con questo riesco a raggruppare le righe per ordine e per ogni ordine creare un
     * DTO con la lista dei suoi prodotti.
     */
    private List<ListaOrdiniEProdottiByTavoloResponseDto> costruzioneDettagliOrdine(List<OrdiniProdottiEntity> righe) {
        // Raggruppo tutte le righe per ID ordine
        // Risultato: una mappa dove la chiave è l'ID ordine e il valore è la lista delle sue righe
        Map<Long, List<OrdiniProdottiEntity>> byOrdine = righe.stream()
                .collect(Collectors.groupingBy(r -> r.getOrdine().getIdOrdine()));

        // Per ogni gruppo di righe (che rappresenta un ordine)
        return byOrdine.values().stream().map(righeOrdine -> {
            // Prendo la prima riga per avere i dati base dell'ordine
            // (tutte le righe hanno gli stessi dati ordine, cambiano solo i prodotti)
            ListaOrdiniEProdottiByTavoloResponseDto dtoBase =
                    ordiniMapper.ordiniProdottiEntityToDto(righeOrdine.get(0));

            // Creo la lista dei prodotti scorrendo tutte le righe di questo ordine
            List<ProdottiOrdinatiResponseDto> prodotti = righeOrdine.stream().map(e -> {
                // Per ogni riga creo un DTO prodotto
                ProdottiOrdinatiResponseDto p = new ProdottiOrdinatiResponseDto();
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
    private boolean processaSingoloProdotto(Long idOrdine, OrdiniEntity ordine, ProdottiOrdinatiRequestDto prodotto) {

        // Controllo che il prodotto esista nel database
        ProdottiEntity prodottoEntity = prodottiRepository.findById(prodotto.getIdProdotto())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Prodotto con ID " + prodotto.getIdProdotto() + " non trovato nel menu"
                ));

        // Creo la chiave per controllare se il prodotto è già nell'ordine
        OrdiniProdottiId chiave = new OrdiniProdottiId(idOrdine, prodotto.getIdProdotto());

        // Controllo se il prodotto è già presente nell'ordine
        if (ordiniProdottiRepository.existsById(chiave)) {
            // CASO 1: Prodotto già presente - aggiorno la quantità sommando
            OrdiniProdottiEntity ordineEsistente = ordiniProdottiRepository.findById(chiave).get();
            Integer quantitaEsistente = ordineEsistente.getQuantitaProdotto();
            Integer quantitaDaAggiungere = prodotto.getQuantitaProdotto();
            Integer quantitaModificataFinale = quantitaEsistente + quantitaDaAggiungere;

            ordineEsistente.setQuantitaProdotto(quantitaModificataFinale);
            ordiniProdottiRepository.save(ordineEsistente);

        } else {
            // CASO 2: Prodotto nuovo - creo una nuova relazione ordine-prodotto
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
    private boolean processaRimozioneSingoloProdotto(Long idOrdine, ProdottiDaRimuovereDto prodotto) {

        // Creo la chiave per trovare il prodotto nell'ordine
        OrdiniProdottiId chiave = new OrdiniProdottiId(idOrdine, prodotto.getIdProdotto());

        // Controllo se il prodotto è presente nell'ordine
        OrdiniProdottiEntity ordineEsistente = ordiniProdottiRepository.findById(chiave)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Prodotto con ID " + prodotto.getIdProdotto() + " non presente nell'ordine"
                ));

        int quantitaAttuale = ordineEsistente.getQuantitaProdotto();
        int quantitaDaRimuovere = prodotto.getQuantitaDaRimuovere();

        // Controllo che non stia provando a rimuovere più di quello che c'è
        if (quantitaDaRimuovere > quantitaAttuale) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantità da rimuovere (" + quantitaDaRimuovere +
                            ") maggiore di quella presente (" + quantitaAttuale + ")"
            );
        }

        int quantitaFinale = quantitaAttuale - quantitaDaRimuovere;

        if (quantitaFinale == 0) {
            // CASO 1: Rimuovo tutto - cancello la riga
            ordiniProdottiRepository.delete(ordineEsistente);
        } else {
            // CASO 2: Rimozione parziale - aggiorno la quantità
            ordineEsistente.setQuantitaProdotto(quantitaFinale);
            ordiniProdottiRepository.save(ordineEsistente);
        }

        return true;
    }

    /**
     * Metodo di aiuto: crea il DTO risultato con le informazioni complete
     * su cosa è andato bene e cosa è andato storto nella modifica.
     */
    private RisultatoModificaOrdineDto creaRisultatoModifica(
            ListaOrdiniEProdottiByTavoloResponseDto ordine,
            int prodottiAggiunti,
            int prodottiRimossi,
            List<String> errori,
            ModificaOrdineRequestDto requestDto) {

        boolean operazioneCompleta = errori.isEmpty();
        String messaggio = costruisciMessaggio(requestDto, prodottiAggiunti, prodottiRimossi, errori, operazioneCompleta);

        return new RisultatoModificaOrdineDto(ordine, prodottiAggiunti, errori, operazioneCompleta, messaggio);
    }

    /**
     * Costruisce il messaggio dinamico in base alle operazioni effettuate
     */
    private String costruisciMessaggio(ModificaOrdineRequestDto requestDto, int prodottiAggiunti,
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

}
