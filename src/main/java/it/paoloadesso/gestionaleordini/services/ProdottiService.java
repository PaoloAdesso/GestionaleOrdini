package it.paoloadesso.gestionaleordini.services;

import it.paoloadesso.gestionaleordini.dto.ProdottiDTO;
import it.paoloadesso.gestionaleordini.entities.ProdottiEntity;
import it.paoloadesso.gestionaleordini.mapper.ProdottiMapper;
import it.paoloadesso.gestionaleordini.repositories.ProdottiRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

    public List<ProdottiDTO> getAllProdotti() {
        List<ProdottiEntity> entities = prodottiRepository.findAll();

        return entities.stream()
                .map(prodottiMapper::prodottiEntityToDto)
                .toList();
    }

    public List<String> getAllCategorie() {
        return prodottiRepository.findAllCategorieDistinct();
    }

    /**
     * Questo metodo cerca prodotti il cui nome contiene la stringa passata.
     * Ad esempio: se cerco "pizza" trovo "Pizza Margherita", "Pizza 4 Formaggi", ecc.
     * La ricerca è case-insensitive (non fa differenza tra maiuscole e minuscole).
     */
    public List<ProdottiDTO> getProdottiByContainingNome(@NotBlank String nomeProdotto) {
        if (nomeProdotto == null || nomeProdotto.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il nome del prodotto non può essere vuoto.");
        }
        List<ProdottiEntity> entities = prodottiRepository.findByNomeContainingIgnoreCase(nomeProdotto.trim());
        return entities.stream()
                .map(prodottiMapper::prodottiEntityToDto)
                .toList();
    }

    public List<ProdottiDTO> getProdottiByContainingCategoria(String nomeCategoria) {
        if (nomeCategoria == null || nomeCategoria.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il nome della categoria non può essere vuoto.");
        }

        List<ProdottiEntity> entities = prodottiRepository.findByCategoriaContainingIgnoreCase(nomeCategoria.trim());
        return entities.stream()
                .map(prodottiMapper::prodottiEntityToDto)
                .toList();
    }
}
