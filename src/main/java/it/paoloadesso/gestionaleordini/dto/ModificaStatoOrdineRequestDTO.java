package it.paoloadesso.gestionaleordini.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.paoloadesso.gestionaleordini.enums.StatoOrdine;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public class ModificaStatoOrdineRequestDTO {

    @NotNull(message = "Lo stato ordine è obbligatorio")
    private StatoOrdine nuovoStato;

    private String note;

    public ModificaStatoOrdineRequestDTO() {}

    public ModificaStatoOrdineRequestDTO(StatoOrdine nuovoStato, String note) {
        this.nuovoStato = nuovoStato;
        this.note = note;
    }

    // Getter e Setter
    public StatoOrdine getNuovoStato() { return nuovoStato; }
    public void setNuovoStato(StatoOrdine nuovoStato) { this.nuovoStato = nuovoStato; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    @Override
    public String toString() {
        return "ModificaStatoOrdineRequestDTO{" +
                "nuovoStato=" + nuovoStato +
                ", note='" + note + '\'' +
                '}';
    }

    @JsonIgnore
    @AssertTrue(message = "Non è possibile chiudere l'ordine con questo endpoint. Usa l'endpoint di chiusura dedicato.")
    public boolean isStatoValidoPerModifica() {
        return nuovoStato != StatoOrdine.CHIUSO;
    }
}
