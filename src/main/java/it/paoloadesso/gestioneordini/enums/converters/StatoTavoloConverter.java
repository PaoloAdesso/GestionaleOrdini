package it.paoloadesso.gestioneordini.enums.converters;

import it.paoloadesso.gestioneordini.enums.StatoTavolo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatoTavoloConverter implements AttributeConverter<StatoTavolo, String> {

    @Override
    public String convertToDatabaseColumn(StatoTavolo stato) {
        return switch (stato) {
            case LIBERO -> "LIBERO";
            case OCCUPATO -> "OCCUPATO";
            case RISERVATO -> "RISERVATO";
            default -> throw new IllegalArgumentException("StatoTavolo sconosciuto: " + stato);
        };
    }

    @Override
    public StatoTavolo convertToEntityAttribute(String dbData) {
        return switch (dbData) {
            case "LIBERO" -> StatoTavolo.LIBERO;
            case "OCCUPATO" -> StatoTavolo.OCCUPATO;
            case "RISERVATO" -> StatoTavolo.RISERVATO;
            default -> throw new IllegalArgumentException("Valore DB StatoTavolo sconosciuto: " + dbData);
        };
    }
}
