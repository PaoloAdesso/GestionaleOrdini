package it.paoloadesso.gestioneordini.controllers;

import it.paoloadesso.gestioneordini.dto.CreaTavoliDto;
import it.paoloadesso.gestioneordini.dto.TavoliDto;
import it.paoloadesso.gestioneordini.services.TavoliService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("tavoli")
@Validated
public class TavoliController {
    private final TavoliService tavoliService;

    public TavoliController(TavoliService tavoliService) {
        this.tavoliService = tavoliService;
    }

    @PostMapping
    public ResponseEntity<TavoliDto> creaTavolo(@RequestBody @Valid CreaTavoliDto tavolo) {
        return ResponseEntity.ok(tavoliService.creaTavolo(tavolo));
    }

    @GetMapping
    public ResponseEntity<List<TavoliDto>> getListaTavoli() {
        return ResponseEntity.ok(tavoliService.getTavoli());
    }

    @GetMapping("/liberi")
    public ResponseEntity<List<TavoliDto>> getListaTavoliLiberi() {
        return ResponseEntity.ok(tavoliService.getTavoliLiberi());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TavoliDto> aggiornaTavolo(
            @PathVariable Long id,
            @RequestBody @Valid TavoliDto tavolo
    ) {
        tavolo.setId(id);
        return ResponseEntity.ok(tavoliService.aggiornaTavolo(tavolo));
    }

    @DeleteMapping("/{idTavolo}")
    public ResponseEntity<Void> deleteTavolo(@PathVariable Long idTavolo){
        tavoliService.deleteTavoloById(idTavolo);
        return ResponseEntity.noContent().build();
    }
}
