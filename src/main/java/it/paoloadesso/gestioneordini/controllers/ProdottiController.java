package it.paoloadesso.gestioneordini.controllers;

import it.paoloadesso.gestioneordini.dto.CreaProdottiDto;
import it.paoloadesso.gestioneordini.dto.ProdottiDto;
import it.paoloadesso.gestioneordini.services.ProdottiService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("prodotti")
@Validated
public class ProdottiController {

    private final ProdottiService prodottiService;

    public ProdottiController(ProdottiService prodottiService) {
        this.prodottiService = prodottiService;
    }

    @PostMapping("/creaProdotto")
    public ResponseEntity<ProdottiDto> creaProdotto(@Valid @RequestBody CreaProdottiDto prodotto) {
        return ResponseEntity.ok(prodottiService.creaProdotto(prodotto));
    }

}
