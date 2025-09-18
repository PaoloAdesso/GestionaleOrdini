package it.paoloadesso.gestioneordini.dto;

import it.paoloadesso.gestioneordini.entities.TavoliEntity;
import it.paoloadesso.gestioneordini.enums.StatoOrdine;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class OrdiniDto {

    @NotNull(message = "L'id ordine è obbligatorio")
    private Long idOrdine;

    @NotNull(message = "L'id tavolo è obbligatorio")
    private Long idTavolo;

    @NotNull(message = "La data ordine è obbligatoria")
    private LocalDate dataOrdine;

    @NotNull(message = "Lo stato ordine è obbligatorio")
    private StatoOrdine statoOrdine;

    public OrdiniDto() {
    }

    public OrdiniDto(Long idOrdine, Long idTavolo, LocalDate dataOrdine, StatoOrdine statoOrdine) {
        this.idOrdine = idOrdine;
        this.idTavolo = idTavolo;
        this.dataOrdine = dataOrdine;
        this.statoOrdine = statoOrdine;
    }

    public Long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(Long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public Long getIdTavolo() {
        return idTavolo;
    }

    public void setIdTavolo(Long idTavolo) {
        this.idTavolo = idTavolo;
    }

    public LocalDate getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(LocalDate dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    public StatoOrdine getStatoOrdine() {
        return statoOrdine;
    }

    public void setStatoOrdine(StatoOrdine statoOrdine) {
        this.statoOrdine = statoOrdine;
    }
}
