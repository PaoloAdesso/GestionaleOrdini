package it.paoloadesso.gestioneordini.mapper;

import it.paoloadesso.gestioneordini.dto.AggiornaTavoliRequestDto;
import it.paoloadesso.gestioneordini.dto.TavoliResponseDto;
import it.paoloadesso.gestioneordini.entities.TavoliEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TavoliMapper {
    TavoliResponseDto entityToDto(TavoliEntity tavoliEntity);

    @Mapping(target = "id", ignore = true)
    TavoliEntity dtoToEntity(AggiornaTavoliRequestDto aggiornaTavoliRequestDto);


}
