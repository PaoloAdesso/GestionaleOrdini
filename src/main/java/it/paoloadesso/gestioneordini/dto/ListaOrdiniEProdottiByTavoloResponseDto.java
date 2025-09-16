package it.paoloadesso.gestioneordini.dto;

import it.paoloadesso.gestioneordini.enums.StatoOrdine;

import java.time.LocalDate;
import java.util.List;

public class ListaOrdiniEProdottiByTavoloResponseDto {

    private Long idOrdine;
    private Long idTavolo;
    private LocalDate dataOrdine;
    private StatoOrdine statoOrdine;

    private List<ProdottiOrdinatiResponseDto> listaOrdineERelativiProdotti;

    public ListaOrdiniEProdottiByTavoloResponseDto(Long idOrdine, Long idTavolo, LocalDate dataOrdine, StatoOrdine statoOrdine, List<ProdottiOrdinatiResponseDto> listaOrdineERelativiProdotti) {
        this.idOrdine = idOrdine;
        this.idTavolo = idTavolo;
        this.dataOrdine = dataOrdine;
        this.statoOrdine = statoOrdine;
        this.listaOrdineERelativiProdotti = listaOrdineERelativiProdotti;
    }

    public ListaOrdiniEProdottiByTavoloResponseDto() {
    }

    public Long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(Long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public Long getIdTavolo() {
        return idTavolo;
    }

    public void setIdTavolo(Long idTavolo) {
        this.idTavolo = idTavolo;
    }

    public LocalDate getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(LocalDate dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    public StatoOrdine getStatoOrdine() {
        return statoOrdine;
    }

    public void setStatoOrdine(StatoOrdine statoOrdine) {
        this.statoOrdine = statoOrdine;
    }
    public List<ProdottiOrdinatiResponseDto> getListaOrdineERelativiProdotti() {
        return listaOrdineERelativiProdotti;
    }

    public void setListaOrdineERelativiProdotti(List<ProdottiOrdinatiResponseDto> listaOrdineERelativiProdotti) {
        this.listaOrdineERelativiProdotti = listaOrdineERelativiProdotti;
    }
}
