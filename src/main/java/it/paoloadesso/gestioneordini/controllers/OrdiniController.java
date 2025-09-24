package it.paoloadesso.gestioneordini.controllers;

import it.paoloadesso.gestioneordini.dto.*;
import it.paoloadesso.gestioneordini.services.OrdiniService;
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
    public ResponseEntity<OrdiniDto> creaOrdine(@RequestBody @Valid CreaOrdiniDto ordine) {
        return ResponseEntity.ok(ordiniService.creaOrdine(ordine));
    }

    @GetMapping("/getListaOrdiniApertiPerTavolo")
    public ResponseEntity<List<OrdiniDto>> getListaOrdiniApertiPerTavolo(
            @RequestParam @NotNull @Positive Long idTavolo) {
        List<OrdiniDto> listaOrdini = ordiniService.getListaOrdiniApertiByTavolo(idTavolo);
        return ResponseEntity.ok(listaOrdini);
    }

    @GetMapping("/getListaTuttiOrdiniAperti")
    public ResponseEntity<List<OrdiniDto>> getListaTuttiOrdiniAperti() {
        return ResponseEntity.ok(ordiniService.getListaTuttiOrdiniAperti());
    }

    @GetMapping("/getListaDettaglioOrdineByIdTavolo")
    public ResponseEntity<List<ListaOrdiniEProdottiByTavoloResponseDto>> getListaDettaglioOrdineByIdTavolo
            (@RequestParam @NotNull @Positive Long idTavolo) {
        return ResponseEntity.ok(ordiniService.getListaDettaglioOrdineByIdTavolo(idTavolo));
    }

    @GetMapping("/getListaDettaglioOrdineDiOggiByIdTavolo")
    public ResponseEntity<List<ListaOrdiniEProdottiByTavoloResponseDto>> getListaDettaglioOrdineDiOggiByIdTavolo
            (@RequestParam @NotNull @Positive Long idTavolo) {
        return ResponseEntity.ok(ordiniService.getListaDettaglioOrdineDiOggiByIdTavolo(idTavolo));
    }

    @GetMapping("getListaOrdiniDiOggi")
    public ResponseEntity<List<OrdiniDto>> getListaOrdiniDiOggi() {
        return ResponseEntity.ok(ordiniService.getListaOrdiniDiOggi());
    }

    @GetMapping("getListaOrdiniDiOggiPerTavolo")
    public ResponseEntity<List<OrdiniDto>> getListaOrdiniDiOggiPerTavolo(
            @RequestParam @NotNull @Positive Long idTavolo) {
        return ResponseEntity.ok(ordiniService.getListaOrdiniDiOggiByTavolo(idTavolo));
    }

}
