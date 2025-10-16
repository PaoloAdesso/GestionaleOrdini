package it.paoloadesso.gestioneordini.controllers;

import it.paoloadesso.gestioneordini.dto.CreaTavoliDTO;
import it.paoloadesso.gestioneordini.dto.TavoliDTO;
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
    public ResponseEntity<TavoliDTO> creaTavolo(@RequestBody @Valid CreaTavoliDTO tavolo) {
        return ResponseEntity.ok(tavoliService.creaTavolo(tavolo));
    }

    @GetMapping
    public ResponseEntity<List<TavoliDTO>> getListaTavoli() {
        return ResponseEntity.ok(tavoliService.getTavoli());
    }

    @GetMapping("/liberi")
    public ResponseEntity<List<TavoliDTO>> getListaTavoliLiberi() {
        return ResponseEntity.ok(tavoliService.getTavoliLiberi());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TavoliDTO> aggiornaTavolo(
            @PathVariable Long id,
            @RequestBody @Valid TavoliDTO tavolo
    ) {
        return ResponseEntity.ok(tavoliService.aggiornaTavolo(id, tavolo));
    }

    @DeleteMapping("/{idTavolo}")
    public ResponseEntity<Void> deleteTavolo(@PathVariable Long idTavolo){
        tavoliService.eliminaTavoloByIdERelativiOrdiniCollegati(idTavolo);
        return ResponseEntity.noContent().build();
    }
}
