package it.paoloadesso.gestioneordini.dto;

import it.paoloadesso.gestioneordini.enums.StatoTavolo;
import jakarta.validation.constraints.NotBlank;

public class CreaTavoliDto {

    @NotBlank(message = "Il nome del tavolo è obbligatorio")
    private String numeroNomeTavolo;

    public CreaTavoliDto(String numeroNomeTavolo, StatoTavolo statoTavolo) {
        this.numeroNomeTavolo = numeroNomeTavolo;
    }

    public CreaTavoliDto() {

    }

    public String getNumeroNomeTavolo() {
        return numeroNomeTavolo;
    }

    public void setNumeroNomeTavolo(String numeroNomeTavolo) {
        this.numeroNomeTavolo = numeroNomeTavolo;
    }

}
