package it.paoloadesso.gestioneordini.controllers;

import it.paoloadesso.gestioneordini.dto.*;
import it.paoloadesso.gestioneordini.services.OrdiniService;
import jakarta.validation.Valid;
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

    @PostMapping
    public ResponseEntity<OrdiniDto> creaOrdine(@RequestBody @Valid CreaOrdiniDto ordine) {
        return ResponseEntity.ok(ordiniService.creaOrdine(ordine));
    }

    @GetMapping
    public ResponseEntity<List<OrdiniDto>> getListaTuttiOrdiniAperti() {
        return ResponseEntity.ok(ordiniService.getListaTuttiOrdiniAperti());
    }

    @GetMapping("/oggi")
    public ResponseEntity<List<OrdiniDto>> getOrdiniDiOggi() {
        return ResponseEntity.ok(ordiniService.getOrdiniDiOggi());
    }

    @GetMapping("/tavolo/{idTavolo}")
    public ResponseEntity<List<OrdiniDto>> getListaOrdiniApertiPerTavolo(@PathVariable @Positive Long idTavolo) {
        List<OrdiniDto> listaOrdini = ordiniService.getListaOrdiniApertiByTavolo(idTavolo);
        return ResponseEntity.ok(listaOrdini);
    }

    @GetMapping("/tavolo/{idTavolo}/oggi")
    public ResponseEntity<List<OrdiniDto>> getOrdiniDiOggiPerTavolo(
            @PathVariable @Positive Long idTavolo) {
        return ResponseEntity.ok(ordiniService.getOrdiniOggiByTavolo(idTavolo));
    }

    @GetMapping("/tavolo/{idTavolo}/dettagli")
    public ResponseEntity<List<ListaOrdiniEProdottiByTavoloResponseDto>> getDettagliOrdiniPerTavolo
            (@PathVariable @Positive Long idTavolo) {
        return ResponseEntity.ok(ordiniService.getDettaglioOrdineByIdTavolo(idTavolo));
    }

    @GetMapping("/tavolo/{idTavolo}/dettagli/oggi")
    public ResponseEntity<List<ListaOrdiniEProdottiByTavoloResponseDto>> getDettagliOrdiniOggiPerTavolo
            (@PathVariable @Positive Long idTavolo) {
        return ResponseEntity.ok(ordiniService.getDettaglioOrdineDiOggiByIdTavolo(idTavolo));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RisultatoModificaOrdineDto> modificaOrdine(
            @PathVariable Long id,
            @Valid @RequestBody ModificaOrdineRequestDto requestDto) {

        RisultatoModificaOrdineDto risultato = ordiniService.modificaOrdine(id, requestDto);

        if (risultato.isOperazioneCompleta()) {
            return ResponseEntity.ok(risultato);
        } else if (risultato.getProdottiAggiunti() > 0) {
            return ResponseEntity.status(207).body(risultato); // 207 - Successo parziale
        } else {
            return ResponseEntity.badRequest().body(risultato); // 400 - Tutti i prodotti falliti
        }
    }
}
