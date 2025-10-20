package it.paoloadesso.gestionaleordini.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.paoloadesso.gestionaleordini.dto.AggiornaTavoloDTO;
import it.paoloadesso.gestionaleordini.dto.CreaTavoliRequestDTO;
import it.paoloadesso.gestionaleordini.dto.TavoliResponseDTO;
import it.paoloadesso.gestionaleordini.services.TavoliService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("tavoli")
@Validated
@Tag(name = "Gestione Tavoli", description = "API per la gestione dei tavoli da parte del personale di sala")
public class TavoliController {
    private final TavoliService tavoliService;

    public TavoliController(TavoliService tavoliService) {
        this.tavoliService = tavoliService;
    }

    @Operation(
            summary = "Crea un nuovo tavolo",
            description = "Permette di aggiungere un nuovo tavolo al ristorante specificando il nome/numero. " +
                    "Il tavolo viene creato automaticamente con stato LIBERO."
    )
    @PostMapping
    public ResponseEntity<TavoliResponseDTO> creaTavolo(@RequestBody @Valid CreaTavoliRequestDTO tavolo) {
        TavoliResponseDTO nuovoTavolo = tavoliService.creaTavolo(tavolo);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuovoTavolo.getId())
                .toUri();

        return ResponseEntity.created(location).body(nuovoTavolo);
    }

    @Operation(
            summary = "Recupera tutti i tavoli",
            description = "Restituisce la lista completa di tutti i tavoli del ristorante " +
                    "con i rispettivi stati (LIBERO, OCCUPATO, RISERVATO). " +
                    "Utile per avere una panoramica completa della sala."
    )
    @GetMapping
    public ResponseEntity<List<TavoliResponseDTO>> getListaTavoli() {
        return ResponseEntity.ok(tavoliService.getTavoli());
    }

    @Operation(
            summary = "Recupera tutti i tavoli liberi",
            description = "Restituisce solo i tavoli con stato LIBERO, disponibili per nuovi clienti. " +
                    "Utile per il personale di sala quando devono far accomodare i clienti."
    )
    @GetMapping("/liberi")
    public ResponseEntity<List<TavoliResponseDTO>> getListaTavoliLiberi() {
        return ResponseEntity.ok(tavoliService.getTavoliLiberi());
    }

    @Operation(
            summary = "Aggiorna un tavolo esistente",
            description = "Permette di modificare le informazioni di un tavolo esistente, " +
                    "come il nome/numero o lo stato (LIBERO, OCCUPATO, RISERVATO). " +
                    "Supporta aggiornamenti parziali: puoi modificare solo i campi che vuoi cambiare " +
                    "inviando solo quelli nel body della richiesta. " +
                    "Nota: lo stato viene gestito automaticamente anche quando si creano/chiudono ordini."
    )
    @PutMapping("/{id}")
    public ResponseEntity<TavoliResponseDTO> aggiornaTavolo(
            @PathVariable Long id,
            @RequestBody @Valid AggiornaTavoloDTO tavolo
    ) {
        return ResponseEntity.ok(tavoliService.aggiornaTavolo(id, tavolo));
    }

    @Operation(
            summary = "Elimina un tavolo",
            description = "Elimina definitivamente un tavolo e tutti gli ordini ad esso collegati. " +
                    "ATTENZIONE: questa operazione è irreversibile e rimuove anche lo storico degli ordini del tavolo. " +
                    "Usare solo per tavoli che non esistono più fisicamente nel ristorante."
    )
    @DeleteMapping("/{idTavolo}")
    public ResponseEntity<Void> deleteTavolo(@PathVariable Long idTavolo){
        tavoliService.eliminaTavoloByIdERelativiOrdiniCollegati(idTavolo);
        return ResponseEntity.noContent().build();
    }
}
