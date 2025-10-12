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
    public ResponseEntity<OrdiniDTO> creaOrdine(@RequestBody @Valid CreaOrdiniDTO ordine) {
        return ResponseEntity.ok(ordiniService.creaOrdine(ordine));
    }

    @GetMapping
    public ResponseEntity<List<OrdiniDTO>> getListaTuttiOrdiniAperti() {
        return ResponseEntity.ok(ordiniService.getListaTuttiOrdiniAperti());
    }

    @GetMapping("/oggi")
    public ResponseEntity<List<OrdiniDTO>> getOrdiniDiOggi() {
        return ResponseEntity.ok(ordiniService.getOrdiniDiOggi());
    }

    @GetMapping("/tavolo/{idTavolo}")
    public ResponseEntity<List<OrdiniDTO>> getListaOrdiniApertiPerTavolo(@PathVariable @Positive Long idTavolo) {
        List<OrdiniDTO> listaOrdini = ordiniService.getListaOrdiniApertiByTavolo(idTavolo);
        return ResponseEntity.ok(listaOrdini);
    }

    @GetMapping("/tavolo/{idTavolo}/oggi")
    public ResponseEntity<List<OrdiniDTO>> getOrdiniDiOggiPerTavolo(
            @PathVariable @Positive Long idTavolo) {
        return ResponseEntity.ok(ordiniService.getOrdiniOggiByTavolo(idTavolo));
    }

    @GetMapping("/tavolo/{idTavolo}/dettagli")
    public ResponseEntity<List<ListaOrdiniEProdottiByTavoloResponseDTO>> getDettagliOrdiniPerTavolo
            (@PathVariable @Positive Long idTavolo) {
        return ResponseEntity.ok(ordiniService.getDettaglioOrdineByIdTavolo(idTavolo));
    }

    @GetMapping("/tavolo/{idTavolo}/dettagli/oggi")
    public ResponseEntity<List<ListaOrdiniEProdottiByTavoloResponseDTO>> getDettagliOrdiniOggiPerTavolo
            (@PathVariable @Positive Long idTavolo) {
        return ResponseEntity.ok(ordiniService.getDettaglioOrdineDiOggiByIdTavolo(idTavolo));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RisultatoModificaOrdineDTO> modificaOrdine(
            @PathVariable Long id,
            @Valid @RequestBody ModificaOrdineRequestDTO requestDto) {

        RisultatoModificaOrdineDTO risultato = ordiniService.modificaOrdine(id, requestDto);

        if (risultato.isOperazioneCompleta()) {
            return ResponseEntity.ok(risultato);
        } else if (risultato.getProdottiAggiunti() > 0) {
            return ResponseEntity.status(207).body(risultato); // 207 - Successo parziale
        } else {
            return ResponseEntity.badRequest().body(risultato); // 400 - Tutti i prodotti falliti
        }
    }
}
