package it.paoloadesso.gestionaleordini.exceptionhandling;

import it.paoloadesso.gestionaleordini.enums.StatoOrdine;

public class TransizioneStatoNonValidaException extends RuntimeException {
    public TransizioneStatoNonValidaException(StatoOrdine statoAttuale, StatoOrdine nuovoStato) {
        super("Transizione non valida da " + statoAttuale + " a " + nuovoStato);
    }
}
