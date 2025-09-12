package it.paoloadesso.gestioneordini.controllers;

import it.paoloadesso.gestioneordini.dto.CreaTavoliDto;
import it.paoloadesso.gestioneordini.dto.AggiornaTavoliRequestDto;
import it.paoloadesso.gestioneordini.dto.TavoliResponseDto;
import it.paoloadesso.gestioneordini.entities.TavoliEntity;
import it.paoloadesso.gestioneordini.services.TavoliService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tavoli")
public class TavoliController {
    private final TavoliService tavoliService;

    public TavoliController(TavoliService tavoliService) {
        this.tavoliService = tavoliService;
    }

    @PostMapping("/creaTavolo")
    public ResponseEntity<TavoliResponseDto> creaTavolo(@Valid @RequestBody CreaTavoliDto tavolo) {
        return ResponseEntity.ok(tavoliService.creaTavolo(tavolo));
    }

    @PutMapping("/aggiornaTavolo")
    public ResponseEntity<TavoliResponseDto> aggiornaTavolo(@RequestBody AggiornaTavoliRequestDto tavolo) {
        return ResponseEntity.ok(tavoliService.aggiornaTavolo(tavolo));
    }
}
