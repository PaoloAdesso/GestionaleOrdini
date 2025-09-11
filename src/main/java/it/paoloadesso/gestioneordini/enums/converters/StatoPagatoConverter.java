package it.paoloadesso.gestioneordini.enums.converters;

import it.paoloadesso.gestioneordini.enums.StatoPagato;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatoPagatoConverter implements AttributeConverter<StatoPagato, String> {

    @Override
    public String convertToDatabaseColumn(StatoPagato stato) {
        return switch (stato) {
            case PAGATO -> "PAGATO";
            case NON_PAGATO -> "NON PAGATO";
            default -> throw new IllegalArgumentException("StatoPagato sconosciuto: " + stato);
        };
    }

    @Override
    public StatoPagato convertToEntityAttribute(String dbData) {
        return switch (dbData) {
            case "PAGATO" -> StatoPagato.PAGATO;
            case "NON PAGATO" -> StatoPagato.NON_PAGATO;
            default -> throw new IllegalArgumentException("Valore DB StatoPagato sconosciuto: " + dbData);
        };
    }
}
