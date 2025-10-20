package it.paoloadesso.gestioneordini.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.paoloadesso.gestioneordini.dto.CreaProdottiDTO;
import it.paoloadesso.gestioneordini.dto.ProdottiDTO;
import it.paoloadesso.gestioneordini.services.ProdottiService;
import jakarta.validation.Valid;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("prodotti")
@Validated
@Tag(name = "Gestione Prodotti", description = "API per la gestione del menu e dei prodotti del ristorante da parte del personale di sala")
public class ProdottiController {

    private final ProdottiService prodottiService;

    public ProdottiController(ProdottiService prodottiService) {
        this.prodottiService = prodottiService;
    }

    @Operation(
            summary = "Crea un nuovo prodotto",
            description = "Permette di aggiungere un nuovo prodotto al menu del ristorante specificando nome, categoria e prezzo. " +
                    "Il prodotto diventa immediatamente disponibile per essere ordinato."
    )
    @PostMapping
    public ResponseEntity<ProdottiDTO> creaProdotto(@RequestBody @Valid CreaProdottiDTO prodotto) {
        ProdottiDTO nuovoProdotto = prodottiService.creaProdotto(prodotto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuovoProdotto.getIdProdotto())
                .toUri();

        return ResponseEntity.created(location).body(nuovoProdotto);
    }

    @Operation(
            summary = "Recupera tutti i prodotti",
            description = "Restituisce la lista completa di tutti i prodotti disponibili nel menu del ristorante " +
                    "con informazioni su nome, categoria e prezzo. Utile per visualizzare l'intero catalogo."
    )
    @GetMapping
    public ResponseEntity<List<ProdottiDTO>> getAllProdotti() {
        return ResponseEntity.ok(prodottiService.getAllProdotti());
    }

    @Operation(
            summary = "Recupera tutte le categorie",
            description = "Restituisce la lista di tutte le categorie di prodotti presenti nel menu. " +
                    "Utile per creare filtri o menu categorizzati nell'interfaccia utente."
    )
    @GetMapping("/categorie")
    public ResponseEntity<List<String>> getAllCategorie() {
        return ResponseEntity.ok(prodottiService.getAllCategorie());
    }

    @Operation(
            summary = "Cerca prodotti per nome",
            description = "Cerca prodotti il cui nome contiene la stringa specificata (ricerca case-insensitive). " +
                    "Utile per il personale di sala quando deve cercare rapidamente un prodotto " +
                    "senza conoscerne il nome esatto. Esempio: 'pizza' trova 'Pizza Margherita', 'Pizza Diavola', ecc."
    )
    @GetMapping("/cerca")
    public ResponseEntity<List<ProdottiDTO>> cercaProdottiPerNome(
            @RequestParam @NotBlank String nomeProdotto) {
        return ResponseEntity.ok(prodottiService.getProdottiByContainingNome(nomeProdotto));
    }

    @Operation(
            summary = "Cerca prodotti per categoria",
            description = "Restituisce tutti i prodotti che appartengono alla categoria specificata (case-insensitive). " +
                    "Utile per filtrare il menu per tipo di prodotto. " +
                    "Esempi di categorie: Antipasti, Primi, Secondi, Dolci, Bevande."
    )
    @GetMapping("/cerca/categoria")
    public ResponseEntity<List<ProdottiDTO>> cercaProdottiPerCategoria(
            @RequestParam @NotBlank String nomeCategoria) {
        return ResponseEntity.ok(prodottiService.getProdottiByContainingCategoria(nomeCategoria));
    }
}
