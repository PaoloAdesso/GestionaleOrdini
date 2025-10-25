package it.paoloadesso.gestionaleordini.exceptionhandling;

public class OrdineNotFoundException extends RuntimeException {
    public OrdineNotFoundException(Long ordineId) {
        super("Ordine con ID " + ordineId + " non trovato");
    }
}