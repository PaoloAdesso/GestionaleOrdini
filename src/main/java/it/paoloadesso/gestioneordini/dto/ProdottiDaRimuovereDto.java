package it.paoloadesso.gestioneordini.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProdottiDaRimuovereDto {

    @NotNull
    @Positive
    private Long idProdotto;

    @NotNull
    @Positive
    private Long quantitaDaRimuovere;

    public ProdottiDaRimuovereDto() {}

    public ProdottiDaRimuovereDto(Long idProdotto, Long quantitaDaRimuovere) {
        this.idProdotto = idProdotto;
        this.quantitaDaRimuovere = quantitaDaRimuovere;
    }

    public Long getIdProdotto() { return idProdotto; }
    public void setIdProdotto(Long idProdotto) { this.idProdotto = idProdotto; }

    public Long getQuantitaDaRimuovere() { return quantitaDaRimuovere; }
    public void setQuantitaDaRimuovere(Long quantitaDaRimuovere) { this.quantitaDaRimuovere = quantitaDaRimuovere; }

    @Override
    public String toString() {
        return "ProdottiDaRimuovereDto{" +
                "idProdotto=" + idProdotto +
                ", quantitaDaRimuovere=" + quantitaDaRimuovere +
                '}';
    }
}
