package it.paoloadesso.gestioneordini.dto;

import jakarta.validation.constraints.NotBlank;

public class CreaTavoliDto {

    @NotBlank
    private String numeroNomeTavolo;

    public CreaTavoliDto(String numeroNomeTavolo) {
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
