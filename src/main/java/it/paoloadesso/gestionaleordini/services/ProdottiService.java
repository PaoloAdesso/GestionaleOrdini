package it.paoloadesso.gestionaleordini.services;

import it.paoloadesso.gestionaleordini.dto.ProdottiDTO;
import it.paoloadesso.gestionaleordini.entities.ProdottiEntity;
import it.paoloadesso.gestionaleordini.exceptionhandling.StatoNonValidoException;
import it.paoloadesso.gestionaleordini.mapper.ProdottiMapper;
import it.paoloadesso.gestionaleordini.repositories.ProdottiRepository;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdottiService {

    private static final Logger log = LoggerFactory.getLogger(ProdottiService.class);

    private final ProdottiRepository prodottiRepository;
    private final ProdottiMapper prodottiMapper;

    public ProdottiService(ProdottiRepository prodottiRepository, ProdottiMapper prodottiMapper) {
        this.prodottiRepository = prodottiRepository;
        this.prodottiMapper = prodottiMapper;
    }

    public List<ProdottiDTO> getAllProdotti() {
        log.debug("Richiesta tutti i prodotti attivi");
        List<ProdottiEntity> entities = prodottiRepository.findAll();
        log.info("Trovati {} prodotti attivi", entities.size());

        return entities.stream()
                .map(prodottiMapper::prodottiEntityToDto)
                .toList();
    }

    public List<String> getAllCategorie() {
        log.debug("Richiesta tutte le categorie");
        List<String> categorie = prodottiRepository.findAllCategorieDistinct();
        log.info("Trovate {} categorie distinte", categorie.size());

        return categorie;
    }

    /**
     * Questo metodo cerca prodotti il cui nome contiene la stringa passata.
     * Ad esempio: se cerco "pizza" trovo "Pizza Margherita", "Pizza 4 Formaggi", ecc.
     * La ricerca è case-insensitive (non fa differenza tra maiuscole e minuscole).
     */
    public List<ProdottiDTO> getProdottiByContainingNome(@NotBlank String nomeProdotto) {
        log.debug("Ricerca prodotti per nome: '{}'", nomeProdotto);

        if (nomeProdotto == null || nomeProdotto.trim().isEmpty()) {
            log.warn("Tentativo di ricerca con nome prodotto vuoto");
            throw new StatoNonValidoException("cercare prodotti", "nome prodotto vuoto");
        }

        String nomePulito = nomeProdotto.trim();
        List<ProdottiEntity> entities = prodottiRepository.findByNomeContainingIgnoreCase(nomePulito);
        log.info("Trovati {} prodotti contenenti '{}'", entities.size(), nomePulito);

        return entities.stream()
                .map(prodottiMapper::prodottiEntityToDto)
                .toList();
    }

    public List<ProdottiDTO> getProdottiByContainingCategoria(String nomeCategoria) {
        log.debug("Ricerca prodotti per categoria: '{}'", nomeCategoria);

        if (nomeCategoria == null || nomeCategoria.trim().isEmpty()) {
            log.warn("Tentativo di ricerca con nome categoria vuoto");
            throw new StatoNonValidoException("cercare prodotti", "nome categoria vuoto");
        }

        String categoriaPulita = nomeCategoria.trim();
        List<ProdottiEntity> entities = prodottiRepository.findByCategoriaContainingIgnoreCase(categoriaPulita);
        log.info("Trovati {} prodotti nella categoria '{}'", entities.size(), categoriaPulita);

        return entities.stream()
                .map(prodottiMapper::prodottiEntityToDto)
                .toList();
    }
}
