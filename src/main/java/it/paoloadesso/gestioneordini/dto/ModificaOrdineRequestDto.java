package it.paoloadesso.gestioneordini.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class ModificaOrdineRequestDto {

    @Positive
    private Long nuovoIdTavolo;

    @Valid
    private List<ProdottiOrdinatiRequestDto> prodottiDaAggiungere;

    @Valid
    private List<ProdottiDaRimuovereDto> prodottiDaRimuovere;

    public ModificaOrdineRequestDto(Long nuovoIdTavolo, List<ProdottiOrdinatiRequestDto> prodottiDaAggiungere, List<ProdottiDaRimuovereDto> prodottiDaRimuovere) {
        this.nuovoIdTavolo = nuovoIdTavolo;
        this.prodottiDaAggiungere = prodottiDaAggiungere;
        this.prodottiDaRimuovere = prodottiDaRimuovere;
    }

    public ModificaOrdineRequestDto() {
    }

    public Long getNuovoIdTavolo() {
        return nuovoIdTavolo;
    }

    public void setNuovoIdTavolo(Long nuovoIdTavolo) {
        this.nuovoIdTavolo = nuovoIdTavolo;
    }

    public List<ProdottiOrdinatiRequestDto> getProdottiDaAggiungere() {
        return prodottiDaAggiungere;
    }

    public void setProdottiDaAggiungere(List<ProdottiOrdinatiRequestDto> prodottiDaAggiungere) {
        this.prodottiDaAggiungere = prodottiDaAggiungere;
    }


    public List<ProdottiDaRimuovereDto> getProdottiDaRimuovere() {
        return prodottiDaRimuovere;
    }

    public void setProdottiDaRimuovere(List<ProdottiDaRimuovereDto> prodottiDaRimuovere) {
        this.prodottiDaRimuovere = prodottiDaRimuovere;
    }

    @Override
    public String toString() {
        return "ModificaOrdineRequestDto{" +
                "nuovoIdTavolo=" + nuovoIdTavolo +
                ", prodottiDaAggiungere=" + prodottiDaAggiungere +
                ", prodottiDaRimuovere=" + prodottiDaRimuovere +
                '}';
    }

    public boolean isEmpty() {
        return nuovoIdTavolo == null &&
                (prodottiDaAggiungere == null || prodottiDaAggiungere.isEmpty()) &&
                (prodottiDaRimuovere == null || prodottiDaRimuovere.isEmpty());
    }
}
