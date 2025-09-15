package it.paoloadesso.gestioneordini.dto;

import it.paoloadesso.gestioneordini.enums.StatoTavolo;
import jakarta.validation.constraints.NotNull;

public class TavoliDto {

    @NotNull
    private Long id;

    private String numeroNomeTavolo;

    private StatoTavolo statoTavolo;

    public TavoliDto(Long id, String numeroNomeTavolo, StatoTavolo statoTavolo) {
        this.id = id;
        this.numeroNomeTavolo = numeroNomeTavolo;
        this.statoTavolo = statoTavolo;
    }

    public TavoliDto() {
        this.statoTavolo = StatoTavolo.LIBERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroNomeTavolo() {
        return numeroNomeTavolo;
    }

    public void setNumeroNomeTavolo(String numeroNomeTavolo) {
        this.numeroNomeTavolo = numeroNomeTavolo;
    }

    public StatoTavolo getStatoTavolo() {
        return statoTavolo;
    }

    public void setStatoTavolo(StatoTavolo statoTavolo) {
        this.statoTavolo = statoTavolo;
    }
}
