package it.paoloadesso.gestioneordini.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProdottiOrdinatiRequestDto {

    @NotNull
    private Long idProdotto;

    @NotNull
    @Positive
    private Long quantitaProdotto;

    public Long getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(Long idProdotto) {
        this.idProdotto = idProdotto;
    }

    public Long getQuantitaProdotto() {
        return quantitaProdotto;
    }

    public void setQuantitaProdotto(Long quantitaProdotto) {
        this.quantitaProdotto = quantitaProdotto;
    }

    public ProdottiOrdinatiRequestDto() {
    }

    public ProdottiOrdinatiRequestDto(Long idProdotto, Long quantitaProdotto) {
        this.idProdotto = idProdotto;
        this.quantitaProdotto = quantitaProdotto;
    }
}
