package it.paoloadesso.gestioneordini.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProdottiOrdinatiRequestDto {

    @NotNull
    private Long idProdotto;

    @NotNull
    @Positive
    private Integer quantitaProdotto;

    public Long getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(Long idProdotto) {
        this.idProdotto = idProdotto;
    }

    public Integer getQuantitaProdotto() {
        return quantitaProdotto;
    }

    public void setQuantitaProdotto(Integer quantitaProdotto) {
        this.quantitaProdotto = quantitaProdotto;
    }

    public ProdottiOrdinatiRequestDto() {
    }

    public ProdottiOrdinatiRequestDto(Long idProdotto, Integer quantitaProdotto) {
        this.idProdotto = idProdotto;
        this.quantitaProdotto = quantitaProdotto;
    }
}
