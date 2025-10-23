package it.paoloadesso.gestionaleordini.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.paoloadesso.gestionaleordini.dto.ProdottiDTO;
import it.paoloadesso.gestionaleordini.services.ProdottiService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            summary = "Elenco prodotti",
            description = "Restituisce la lista completa di tutti i prodotti disponibili nel menu del ristorante " +
                    "con informazioni su nome, categoria e prezzo. Utile per visualizzare l'intero catalogo."
    )
    @GetMapping
    public ResponseEntity<List<ProdottiDTO>> getAllProdotti() {
        return ResponseEntity.ok(prodottiService.getAllProdotti());
    }

    @Operation(
            summary = "Categorie menu disponibili",
            description = "Restituisce la lista di tutte le categorie di prodotti presenti nel menu. " +
                    "Utile per creare filtri o menu categorizzati nell'interfaccia utente."
    )
    @GetMapping("/categorie")
    public ResponseEntity<List<String>> getAllCategorie() {
        return ResponseEntity.ok(prodottiService.getAllCategorie());
    }

    @Operation(
            summary = "Ricerca rapida prodotti per nome",
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
            summary = "Ricerca rapida prodotti per categoria",
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
