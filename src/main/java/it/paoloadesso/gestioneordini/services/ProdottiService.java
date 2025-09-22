package it.paoloadesso.gestioneordini.services;

import it.paoloadesso.gestioneordini.dto.*;
import it.paoloadesso.gestioneordini.entities.OrdiniEntity;
import it.paoloadesso.gestioneordini.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestioneordini.entities.ProdottiEntity;
import it.paoloadesso.gestioneordini.entities.TavoliEntity;
import it.paoloadesso.gestioneordini.entities.keys.OrdiniProdottiId;
import it.paoloadesso.gestioneordini.enums.StatoOrdine;
import it.paoloadesso.gestioneordini.mapper.OrdiniMapper;
import it.paoloadesso.gestioneordini.mapper.ProdottiMapper;
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
public class ProdottiService {

    private final ProdottiRepository prodottiRepository;
    private final ProdottiMapper prodottiMapper;

    public ProdottiService(ProdottiRepository prodottiRepository, ProdottiMapper prodottiMapper) {
        this.prodottiRepository = prodottiRepository;
        this.prodottiMapper = prodottiMapper;
    }

    public ProdottiDto creaProdotto(CreaProdottiDto dto) {
        //controllo se il prodotto esiste
        if (prodottiRepository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il prodotto «" + dto.getNome() + "» è già presente");
        }
        if (dto.getNome() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inserire il nome del prodotto perché non può essere vuoto");
        }


        ProdottiEntity prodotto = prodottiRepository.save(prodottiMapper.createProdottiDtoToEntity(dto));
        return prodottiMapper.createProdottiEntityToDto(prodotto);
    }
}
