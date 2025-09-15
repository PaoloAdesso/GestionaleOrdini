package it.paoloadesso.gestioneordini.controllers;

import it.paoloadesso.gestioneordini.dto.CreaTavoliDto;
import it.paoloadesso.gestioneordini.dto.TavoliDto;
import it.paoloadesso.gestioneordini.services.TavoliService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("tavoli")
public class TavoliController {
    private final TavoliService tavoliService;

    public TavoliController(TavoliService tavoliService) {
        this.tavoliService = tavoliService;
    }

    @PostMapping("/creaTavolo")
    public ResponseEntity<TavoliDto> creaTavolo(@Valid @RequestBody CreaTavoliDto tavolo) {
        return ResponseEntity.ok(tavoliService.creaTavolo(tavolo));
    }

    @PutMapping("/aggiornaTavolo")
    public ResponseEntity<TavoliDto> aggiornaTavolo(@RequestBody TavoliDto tavolo) {
        return ResponseEntity.ok(tavoliService.aggiornaTavolo(tavolo));
    }

    @GetMapping("/getListaTavoli")
    public ResponseEntity<List<TavoliDto>> getListaTavoli() {
        return ResponseEntity.ok(tavoliService.getTavoli());
    }

    @DeleteMapping("/{idTavolo}")
    public ResponseEntity<Void> deleteTavolo(@PathVariable Long idTavolo){
        tavoliService.deleteTavoloById(idTavolo);
        return ResponseEntity.noContent().build();
    }
}
