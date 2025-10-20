package it.paoloadesso.gestionaleordini.dto;

import it.paoloadesso.gestionaleordini.enums.StatoTavolo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TavoliResponseDTO {

    @NotNull
    private Long id;

    @NotBlank
    private String numeroNomeTavolo;

    @NotNull
    private StatoTavolo statoTavolo;

    public TavoliResponseDTO(Long id, String numeroNomeTavolo, StatoTavolo statoTavolo) {
        this.id = id;
        this.numeroNomeTavolo = numeroNomeTavolo;
        this.statoTavolo = statoTavolo;
    }

    public TavoliResponseDTO() {
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
