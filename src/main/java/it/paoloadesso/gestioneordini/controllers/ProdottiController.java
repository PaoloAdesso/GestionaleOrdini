package it.paoloadesso.gestioneordini.controllers;

import it.paoloadesso.gestioneordini.dto.CreaProdottiDTO;
import it.paoloadesso.gestioneordini.dto.ProdottiDTO;
import it.paoloadesso.gestioneordini.services.ProdottiService;
import jakarta.validation.Valid;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("prodotti")
@Validated
public class ProdottiController {

    private final ProdottiService prodottiService;

    public ProdottiController(ProdottiService prodottiService) {
        this.prodottiService = prodottiService;
    }

    @PostMapping
    public ResponseEntity<ProdottiDTO> creaProdotto(@RequestBody @Valid CreaProdottiDTO prodotto) {
        return ResponseEntity.ok(prodottiService.creaProdotto(prodotto));
    }

    @GetMapping
    public ResponseEntity<List<ProdottiDTO>> getAllProdotti() {
        return ResponseEntity.ok(prodottiService.getAllProdotti());
    }

    @GetMapping("/categorie")
    public ResponseEntity<List<String>> getAllCategorie() {
        return ResponseEntity.ok(prodottiService.getAllCategorie());
    }

    @GetMapping("/cerca")
    public ResponseEntity<List<ProdottiDTO>> cercaProdottiPerNome(
            @RequestParam @NotBlank String nomeProdotto) {
        return ResponseEntity.ok(prodottiService.getProdottiByContainingNome(nomeProdotto));
    }

    @GetMapping("/cerca/categoria")
    public ResponseEntity<List<ProdottiDTO>> cercaProdottiPerCategoria(
            @RequestParam @NotBlank String nomeCategoria) {
        return ResponseEntity.ok(prodottiService.getProdottiByContainingCategoria(nomeCategoria));
    }
}
