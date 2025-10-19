package it.paoloadesso.gestioneordini.dto;

import it.paoloadesso.gestioneordini.enums.StatoTavolo;

public class AggiornaTavoloDTO {

    private String numeroNomeTavolo;
    private StatoTavolo statoTavolo;

    public AggiornaTavoloDTO() {
    }

    public AggiornaTavoloDTO(String numeroNomeTavolo, StatoTavolo statoTavolo) {
        this.numeroNomeTavolo = numeroNomeTavolo;
        this.statoTavolo = statoTavolo;
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
