package it.paoloadesso.gestioneordini.enums.converters;

import it.paoloadesso.gestioneordini.enums.StatoOrdine;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatoOrdineConverter implements AttributeConverter<StatoOrdine, String> {

    @Override
    public String convertToDatabaseColumn(StatoOrdine stato) {
        return switch (stato) {
            case IN_ATTESA -> "IN ATTESA";
            case IN_PREPARAZIONE -> "IN PREPARAZIONE";
            case SERVITO -> "SERVITO";
            case CHIUSO -> "CHIUSO";
            default -> throw new IllegalArgumentException("StatoOrdine sconosciuto: " + stato);
        };
    }

    @Override
    public StatoOrdine convertToEntityAttribute(String dbData) {
        return switch (dbData) {
            case "IN ATTESA" -> StatoOrdine.IN_ATTESA;
            case "IN PREPARAZIONE" -> StatoOrdine.IN_PREPARAZIONE;
            case "SERVITO" -> StatoOrdine.SERVITO;
            case "CHIUSO" -> StatoOrdine.CHIUSO;
            default -> throw new IllegalArgumentException("Valore DB StatoOrdine sconosciuto: " + dbData);
        };
    }
}
