package it.paoloadesso.gestioneordini.controllers;

import it.paoloadesso.gestioneordini.dto.*;
import it.paoloadesso.gestioneordini.mapper.TavoliMapper;
import it.paoloadesso.gestioneordini.services.OrdiniService;
import it.paoloadesso.gestioneordini.services.TavoliService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("ordini")
public class OrdiniController {

    private final OrdiniService ordiniService;

    public OrdiniController(OrdiniService ordiniService) {
        this.ordiniService = ordiniService;
    }

    @PostMapping("/creaOrdine")
    public ResponseEntity<OrdiniDto> creaOrdine(@Valid @RequestBody CreaOrdiniDto ordine) {
        return ResponseEntity.ok(ordiniService.creaOrdine(ordine));
    }

    @GetMapping("/getListaOrdiniApertiPerTavolo")
    public ResponseEntity<List<OrdiniDto>> getListaOrdiniApertiPerTavolo(@RequestParam Long idTavolo) {
        List<OrdiniDto> listaOrdini = ordiniService.getListaOrdiniApertiByTavolo(idTavolo);
        return ResponseEntity.ok(listaOrdini);
    }
}
