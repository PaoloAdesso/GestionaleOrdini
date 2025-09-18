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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
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

    public OrdiniDto creaOrdine(CreaOrdiniDto dto) {
        if (!tavoliRepository.existsById(dto.getIdTavolo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ordine non creato poiché il tavolo non esiste.");
        }

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
        if (!tavoliRepository.existsById(idTavolo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tavolo non trovato");
        }

        List<OrdiniEntity> ordini = ordiniRepository.findByTavoloIdAndStatoOrdineNot(idTavolo, StatoOrdine.CHIUSO);

        return ordini.stream()
                .map(el -> ordiniMapper.ordiniEntityToDto(el))
                .collect(Collectors.toList());
    }

    public List<OrdiniDto> getListaTuttiOrdiniAperti() {
        List<OrdiniEntity> ordini = ordiniRepository.findByStatoOrdineNot(StatoOrdine.CHIUSO);
        return ordini.stream()
                .map(el -> ordiniMapper.ordiniEntityToDto(el))
                .collect(Collectors.toList());
    }


}
