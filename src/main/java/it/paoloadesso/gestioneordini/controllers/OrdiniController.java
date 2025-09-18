package it.paoloadesso.gestioneordini.controllers;

import it.paoloadesso.gestioneordini.dto.*;
import it.paoloadesso.gestioneordini.enums.StatoOrdine;
import it.paoloadesso.gestioneordini.mapper.TavoliMapper;
import it.paoloadesso.gestioneordini.services.OrdiniService;
import it.paoloadesso.gestioneordini.services.TavoliService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("ordini")
@Validated
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
    public ResponseEntity<List<OrdiniDto>> getListaOrdiniApertiPerTavolo(
            @RequestParam @NotNull(message = "L'id del tavolo è obbligatorio")
            @Positive(message = "L'id del tavolo deve essere un numero positivo") Long idTavolo) {
        List<OrdiniDto> listaOrdini = ordiniService.getListaOrdiniApertiByTavolo(idTavolo);
        return ResponseEntity.ok(listaOrdini);
    }

    @GetMapping("/getListaTuttiOrdiniAperti")
    public ResponseEntity<List<OrdiniDto>> getListaTuttiOrdiniAperti() {
        return ResponseEntity.ok(ordiniService.getListaTuttiOrdiniAperti());
    }


}
