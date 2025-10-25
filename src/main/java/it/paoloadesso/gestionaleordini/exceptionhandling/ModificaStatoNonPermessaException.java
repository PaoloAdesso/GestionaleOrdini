package it.paoloadesso.gestionaleordini.exceptionhandling;

import it.paoloadesso.gestionaleordini.enums.StatoOrdine;

public class ModificaStatoNonPermessaException extends RuntimeException {
    public ModificaStatoNonPermessaException(String motivo) {
        super(motivo);
    }

    public ModificaStatoNonPermessaException(Long ordineId, StatoOrdine statoAttuale) {
        super("Non è possibile modificare lo stato dell'ordine " + ordineId +
                " che si trova nello stato " + statoAttuale);
    }
}

