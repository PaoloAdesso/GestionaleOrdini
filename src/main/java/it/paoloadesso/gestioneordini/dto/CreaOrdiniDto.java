package it.paoloadesso.gestioneordini.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreaOrdiniDto {
    @NotNull
    private Long idTavolo;

    @NotEmpty
    private List<ProdottiOrdinatiRequestDto> listaProdottiOrdinati;

    public CreaOrdiniDto() {
    }

    public Long getIdTavolo() {
        return idTavolo;
    }

    public void setIdTavolo(Long idTavolo) {
        this.idTavolo = idTavolo;
    }

    public List<ProdottiOrdinatiRequestDto> getListaProdottiOrdinati() {
        return listaProdottiOrdinati;
    }

    public void setListaProdottiOrdinati(List<ProdottiOrdinatiRequestDto> listaProdottiOrdinati) {
        this.listaProdottiOrdinati = listaProdottiOrdinati;
    }

    public CreaOrdiniDto(Long idTavolo, List<ProdottiOrdinatiRequestDto> listaProdottiOrdinati) {
        this.idTavolo = idTavolo;
        this.listaProdottiOrdinati = listaProdottiOrdinati;
    }
}
