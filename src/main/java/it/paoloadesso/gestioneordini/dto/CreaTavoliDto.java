package it.paoloadesso.gestioneordini.dto;

import it.paoloadesso.gestioneordini.enums.StatoTavolo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreaTavoliDto {

    @NotBlank(message = "Il nome del tavolo è obbligatorio")
    private String numeroNomeTavolo;

    @NotNull(message = "Il campo statoTavolo è obbligatorio e non può essere nullo")
    private StatoTavolo statoTavolo;

    public CreaTavoliDto(String numeroNomeTavolo, StatoTavolo statoTavolo) {
        this.numeroNomeTavolo = numeroNomeTavolo;
        this.statoTavolo = statoTavolo;
    }

    public CreaTavoliDto() {
        this.statoTavolo = StatoTavolo.LIBERO;
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
