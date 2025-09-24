package it.paoloadesso.gestioneordini.services;

import it.paoloadesso.gestioneordini.dto.*;
import it.paoloadesso.gestioneordini.entities.ProdottiEntity;
import it.paoloadesso.gestioneordini.mapper.ProdottiMapper;
import it.paoloadesso.gestioneordini.repositories.ProdottiRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProdottiService {

    private final ProdottiRepository prodottiRepository;
    private final ProdottiMapper prodottiMapper;

    public ProdottiService(ProdottiRepository prodottiRepository, ProdottiMapper prodottiMapper) {
        this.prodottiRepository = prodottiRepository;
        this.prodottiMapper = prodottiMapper;
    }

    @Transactional
    public ProdottiDto creaProdotto(CreaProdottiDto dto) {
        //controllo se il prodotto esiste
        if (prodottiRepository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il prodotto «" + dto.getNome() + "» è già presente");
        }

        ProdottiEntity prodotto = prodottiRepository.save(prodottiMapper.createProdottiDtoToEntity(dto));
        return prodottiMapper.prodottiEntityToDto(prodotto);
    }

    public List<ProdottiDto> getAllProdotti() {
        List<ProdottiEntity> entities = prodottiRepository.findAll();

        return entities.stream()
                .map(prodottiMapper::prodottiEntityToDto)
                .toList();
    }

    public List<ProdottiDto> getProdottiByContainingNome(@NotBlank String nomeProdotto) {
        List<ProdottiEntity> entities = prodottiRepository.findByNomeContainingIgnoreCase(nomeProdotto.trim());
        return entities.stream()
                .map(prodottiMapper::prodottiEntityToDto)
                .toList();
    }

    public List<ProdottiDto> getProdottiByContainingCategoria(String nomeCategoria) {
        if (nomeCategoria == null || nomeCategoria.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il nome della categoria non può essere vuoto.");
        }

        List<ProdottiEntity> entities = prodottiRepository.findByCategoriaContainingIgnoreCase(nomeCategoria.trim());
        return entities.stream()
                .map(prodottiMapper::prodottiEntityToDto)
                .toList();
    }
}
