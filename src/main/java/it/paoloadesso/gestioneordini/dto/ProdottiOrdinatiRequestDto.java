package it.paoloadesso.gestioneordini.dto;

public class ProdottiOrdinatiRequestDto {

    private Long idProdotto;

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
