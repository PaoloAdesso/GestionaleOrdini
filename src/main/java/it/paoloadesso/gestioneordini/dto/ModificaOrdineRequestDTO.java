package it.paoloadesso.gestioneordini.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class ModificaOrdineRequestDTO {

    @Positive
    private Long nuovoIdTavolo;

    @Valid
    private List<ProdottiOrdinatiRequestDTO> prodottiDaAggiungere;

    @Valid
    private List<ProdottiDaRimuovereDTO> prodottiDaRimuovere;

    public ModificaOrdineRequestDTO(Long nuovoIdTavolo, List<ProdottiOrdinatiRequestDTO> prodottiDaAggiungere, List<ProdottiDaRimuovereDTO> prodottiDaRimuovere) {
        this.nuovoIdTavolo = nuovoIdTavolo;
        this.prodottiDaAggiungere = prodottiDaAggiungere;
        this.prodottiDaRimuovere = prodottiDaRimuovere;
    }

    public ModificaOrdineRequestDTO() {
    }

    public Long getNuovoIdTavolo() {
        return nuovoIdTavolo;
    }

    public void setNuovoIdTavolo(Long nuovoIdTavolo) {
        this.nuovoIdTavolo = nuovoIdTavolo;
    }

    public List<ProdottiOrdinatiRequestDTO> getProdottiDaAggiungere() {
        return prodottiDaAggiungere;
    }

    public void setProdottiDaAggiungere(List<ProdottiOrdinatiRequestDTO> prodottiDaAggiungere) {
        this.prodottiDaAggiungere = prodottiDaAggiungere;
    }


    public List<ProdottiDaRimuovereDTO> getProdottiDaRimuovere() {
        return prodottiDaRimuovere;
    }

    public void setProdottiDaRimuovere(List<ProdottiDaRimuovereDTO> prodottiDaRimuovere) {
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
