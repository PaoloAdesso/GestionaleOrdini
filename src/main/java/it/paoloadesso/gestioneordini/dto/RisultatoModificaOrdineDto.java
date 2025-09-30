package it.paoloadesso.gestioneordini.dto;

import java.util.List;

/**
 * DTO per gestire i risultati delle modifiche agli ordini.
 * Permette di gestire successi parziali quando alcuni prodotti
 * vengono aggiunti correttamente e altri danno errore.
 */
public class RisultatoModificaOrdineDto {

    private ListaOrdiniEProdottiByTavoloResponseDto ordine;
    private int prodottiAggiunti;
    private List<String> errori;
    private boolean operazioneCompleta;
    private String messaggio;

    public RisultatoModificaOrdineDto() {}

    public RisultatoModificaOrdineDto(ListaOrdiniEProdottiByTavoloResponseDto ordine,
                                      int prodottiAggiunti,
                                      List<String> errori,
                                      boolean operazioneCompleta,
                                      String messaggio) {
        this.ordine = ordine;
        this.prodottiAggiunti = prodottiAggiunti;
        this.errori = errori;
        this.operazioneCompleta = operazioneCompleta;
        this.messaggio = messaggio;
    }

    public ListaOrdiniEProdottiByTavoloResponseDto getOrdine() { return ordine; }
    public void setOrdine(ListaOrdiniEProdottiByTavoloResponseDto ordine) { this.ordine = ordine; }

    public int getProdottiAggiunti() { return prodottiAggiunti; }
    public void setProdottiAggiunti(int prodottiAggiunti) { this.prodottiAggiunti = prodottiAggiunti; }

    public List<String> getErrori() { return errori; }
    public void setErrori(List<String> errori) { this.errori = errori; }

    public boolean isOperazioneCompleta() { return operazioneCompleta; }
    public void setOperazioneCompleta(boolean operazioneCompleta) { this.operazioneCompleta = operazioneCompleta; }

    public String getMessaggio() { return messaggio; }
    public void setMessaggio(String messaggio) { this.messaggio = messaggio; }

    @Override
    public String toString() {
        return "RisultatoModificaOrdineDto{" +
                "prodottiAggiunti=" + prodottiAggiunti +
                ", errori=" + errori +
                ", operazioneCompleta=" + operazioneCompleta +
                ", messaggio='" + messaggio + '\'' +
                '}';
    }
}
