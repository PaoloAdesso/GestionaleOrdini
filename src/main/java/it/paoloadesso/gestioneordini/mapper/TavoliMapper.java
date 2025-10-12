package it.paoloadesso.gestioneordini.mapper;

import it.paoloadesso.gestioneordini.dto.CreaTavoliDTO;
import it.paoloadesso.gestioneordini.dto.TavoliDTO;
import it.paoloadesso.gestioneordini.entities.TavoliEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TavoliMapper {

    CreaTavoliDTO createTavoliEntityToDto(TavoliEntity tavoliEntity);

    @Mapping(target = "id", ignore = true)
    TavoliEntity createTavoliDtoToEntity(CreaTavoliDTO creaTavoliDto);

    TavoliDTO entityToDto(TavoliEntity tavoliEntity);

    @Mapping(target = "id", ignore = true)
    TavoliEntity dtoToEntity(TavoliDTO aggiornaTavoliRequestDto);


}
