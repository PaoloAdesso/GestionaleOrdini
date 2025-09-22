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
@Transactional
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

    public OrdiniDto creaOrdine(CreaOrdiniDto dto) {
        controlloSeIlTavoloEsiste(dto.getIdTavolo());

        TavoliEntity tavolo = tavoliRepository.findById(dto.getIdTavolo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tavolo non trovato"));

        OrdiniEntity ordine = new OrdiniEntity();
        ordine.setTavolo(tavolo);
        ordine = ordiniRepository.save(ordine);

        List<OrdiniProdottiEntity> ordiniProdottiEntities = new ArrayList<>();
        for (ProdottiOrdinatiRequestDto prodottoDto : dto.getListaProdottiOrdinati()) {
            ProdottiEntity prodotto = prodottiRepository.findById(prodottoDto.getIdProdotto())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prodotto con ID " + prodottoDto.getIdProdotto() + " non trovato"));

            OrdiniProdottiId id = new OrdiniProdottiId(ordine.getIdOrdine(), prodotto.getId());
            OrdiniProdottiEntity ordineProdotti = new OrdiniProdottiEntity();
            ordineProdotti.setId(id);
            ordineProdotti.setOrdine(ordine);
            ordineProdotti.setProdotto(prodotto);
            ordineProdotti.setQuantitaProdotto(prodottoDto.getQuantitaProdotto());

            ordiniProdottiEntities.add(ordineProdotti);

        }

        ordiniProdottiRepository.saveAll(ordiniProdottiEntities);

        // Alternativa usando mapStruct e non manuale:
        // return ordiniMapper.ordiniEntityToDto(ordine);

        OrdiniDto ordineDto = new OrdiniDto();
        ordineDto.setIdOrdine(ordine.getIdOrdine());
        ordineDto.setIdTavolo(tavolo.getId());
        ordineDto.setDataOrdine(ordine.getDataOrdine());
        ordineDto.setStatoOrdine(ordine.getStatoOrdine());

        return ordineDto;
    }

    public List<OrdiniDto> getListaOrdiniApertiByTavolo(Long idTavolo) {
        controlloSeIlTavoloEsiste(idTavolo);

        List<OrdiniEntity> ordini = ordiniRepository.findByTavoloIdAndStatoOrdineNot(idTavolo, StatoOrdine.CHIUSO);

        return ordini.stream()
                .map(el -> ordiniMapper.ordiniEntityToDto(el))
                .collect(Collectors.toList());
    }

    private void controlloSeIlTavoloEsiste(Long idTavolo) {
        if (idTavolo == null || !tavoliRepository.existsById(idTavolo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tavolo non trovato");
        }
    }

    public List<OrdiniDto> getListaTuttiOrdiniAperti() {
        List<OrdiniEntity> ordini = ordiniRepository.findByStatoOrdineNot(StatoOrdine.CHIUSO);
        return ordini.stream()
                .map(el -> ordiniMapper.ordiniEntityToDto(el))
                .collect(Collectors.toList());
    }


    /** PSEUDOCODICE
        Obiettivo:
        - Input: idTavolo valido con annotazioni per validazione (@NotNull, @Positive)
        - Output: lista di DTO ordine+prodotti per tutti gli ordini NON chiusi del tavolo

        Poi devo fare:
        - Validazione esistenza tavolo: if + eccezione 404
        - Filtrare le righe “ordine-prodotto” su tavolo e stato non CHIUSO
        - Raggruppamento per idOrdine con groupBy
        - Per ogni gruppo: costruisco DTO dell’ordine con prodotti

        QUINDI:
        - Validazione tavolo:
         se tavolo NON esiste con idTavolo:
         lancia Eccezione NOT_FOUND

        - Recupero righe ordine-prodotto filtrate:
         righe = repository.cercaRighePerTavoloConStatoNonChiuso(idTavolo)

        - Raggruppo righe per idOrdine:
         gruppi = raggruppa righe per riga.ordine.idOrdine

        - Costruisco la lista di DTO ordine+prodotti:
         listaDTO = lista vuota
         per ciascun gruppo in gruppi:
         ordine = prendi ordine dalla prima riga del gruppo
         dtoOrdine = mappa ordine in DTO base

         prodotti = lista vuota
         per ciascuna riga in gruppo:
         prodottoDTO = creaProdottoDTO(riga)
         aggiungi prodottoDTO a prodotti

         dtoOrdine.setProdotti(prodotti)
         aggiungi dtoOrdine a listaDTO

        - Ritorno risultato:
         ritorna listaDTO
     */
    public List<ListaOrdiniEProdottiByTavoloResponseDto> getListaDettaglioOrdineByIdTavolo(
            @NotNull(message = "L'id del tavolo è obbligatorio")
            @Positive(message = "L'id del tavolo deve essere un numero positivo")
            Long idTavolo) {

        controlloSeIlTavoloEsiste(idTavolo);

        List<OrdiniProdottiEntity> righe = ordiniProdottiRepository
                .findByOrdine_Tavolo_IdAndOrdine_StatoOrdineNot(idTavolo, StatoOrdine.CHIUSO);

        return costruzioneDettagliOrdine(righe);
    }

    public List<OrdiniDto> getListaOrdiniDiOggi() {
        List<OrdiniEntity> ordini = ordiniRepository.findByDataOrdineAndStatoOrdineNot(LocalDate.now(), StatoOrdine.CHIUSO);
        return ordini.stream().map(ordiniMapper::ordiniEntityToDto).toList();
    }

    public List<OrdiniDto> getListaOrdiniDiOggiByTavolo(@NotNull @Positive Long idTavolo) {
        controlloSeIlTavoloEsiste(idTavolo);
        List<OrdiniEntity> ordini = ordiniRepository
                .findByTavoloIdAndDataOrdineAndStatoOrdineNot(idTavolo, LocalDate.now(), StatoOrdine.CHIUSO);
        return ordini.stream().map(ordiniMapper::ordiniEntityToDto).toList();
    }

    public List<ListaOrdiniEProdottiByTavoloResponseDto> getListaDettaglioOrdineDiOggiByIdTavolo(
            @NotNull(message = "L'id del tavolo è obbligatorio")
            @Positive(message = "L'id del tavolo deve essere un numero positivo")
            Long idTavolo) {

        controlloSeIlTavoloEsiste(idTavolo);

        List<OrdiniProdottiEntity> righe = ordiniProdottiRepository
                .findByOrdineTavoloIdAndOrdineDataOrdineAndOrdineStatoOrdineNot(idTavolo, LocalDate.now() , StatoOrdine.CHIUSO);

        return costruzioneDettagliOrdine(righe);
    }

    private List<ListaOrdiniEProdottiByTavoloResponseDto> costruzioneDettagliOrdine(List<OrdiniProdottiEntity> righe) {
        Map<Long, List<OrdiniProdottiEntity>> byOrdine = righe.stream()
                .collect(Collectors.groupingBy(r -> r.getOrdine().getIdOrdine()));

        /**     PSEUDOCODICE:
         per ogni gruppo di righeOrdine:
         ordine di riferimento (che è sempre lo stesso in tutte le righe)
         ordine = prendo ordine dalla prima riga

         creo un DTO base con i dati dell'ordine
         dtoBase = mapper.creaDTOdaOrdine(primaRiga)

         dopo: aggiungo la lista prodotti scorrendo tutte le righe
         */
        return byOrdine.values().stream().map(righeOrdine -> {
            ListaOrdiniEProdottiByTavoloResponseDto dtoBase =
                    ordiniMapper.ordiniProdottiEntityToDto(righeOrdine.get(0));
            List<ProdottiOrdinatiResponseDto> prodotti = righeOrdine.stream().map(e -> {
                ProdottiOrdinatiResponseDto p = new ProdottiOrdinatiResponseDto();
                p.setIdProdotto(e.getProdotto().getId());
                p.setQuantitaProdotto(e.getQuantitaProdotto());
                p.setStatoPagato(e.getStatoPagato());
                return p;
            }).toList();
            dtoBase.setListaOrdineERelativiProdotti(prodotti);
            return dtoBase;
        }).toList();
    }
}
