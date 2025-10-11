package it.paoloadesso.gestioneordini.controllers;

import it.paoloadesso.gestioneordini.dto.CreaProdottiDto;
import it.paoloadesso.gestioneordini.dto.ProdottiDto;
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
    public ResponseEntity<ProdottiDto> creaProdotto(@RequestBody @Valid CreaProdottiDto prodotto) {
        return ResponseEntity.ok(prodottiService.creaProdotto(prodotto));
    }

    @GetMapping
    public ResponseEntity<List<ProdottiDto>> getAllProdotti() {
        return ResponseEntity.ok(prodottiService.getAllProdotti());
    }

    @GetMapping("/categorie")
    public ResponseEntity<List<String>> getAllCategorie() {
        return ResponseEntity.ok(prodottiService.getAllCategorie());
    }

    @GetMapping("/cerca")
    public ResponseEntity<List<ProdottiDto>> cercaProdottiPerNome(
            @RequestParam @NotBlank String nomeProdotto) {
        return ResponseEntity.ok(prodottiService.getProdottiByContainingNome(nomeProdotto));
    }

    @GetMapping("/cerca/categoria")
    public ResponseEntity<List<ProdottiDto>> cercaProdottiPerCategoria(
            @RequestParam @NotBlank String nomeCategoria) {
        return ResponseEntity.ok(prodottiService.getProdottiByContainingCategoria(nomeCategoria));
    }
}
