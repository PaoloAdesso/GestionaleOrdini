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

    @PostMapping("/creaProdotto")
    public ResponseEntity<ProdottiDto> creaProdotto(@RequestBody @Valid CreaProdottiDto prodotto) {
        return ResponseEntity.ok(prodottiService.creaProdotto(prodotto));
    }

    @GetMapping("/getAllProdotti")
    public ResponseEntity<List<ProdottiDto>> getAllProdotti() {
        return ResponseEntity.ok(prodottiService.getAllProdotti());
    }

    @GetMapping("/getProdottiByContainingNome")
    public ResponseEntity<List<ProdottiDto>> getProdottiByContainingNome(
            @RequestParam @NotBlank String nomeProdotto) {
        return ResponseEntity.ok(prodottiService.getProdottiByContainingNome(nomeProdotto));
    }

    @GetMapping("/getProdottiByContainingCategoria")
    public ResponseEntity<List<ProdottiDto>> getProdottiByContainingCategoria(
            @RequestParam @NotBlank String nomeCategoria) {
        return ResponseEntity.ok(prodottiService.getProdottiByContainingCategoria(nomeCategoria));
    }

}
