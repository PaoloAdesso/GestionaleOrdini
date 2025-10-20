package it.paoloadesso.gestionaleordini.mapper;

import it.paoloadesso.gestionaleordini.dto.CreaTavoliRequestDTO;
import it.paoloadesso.gestionaleordini.dto.TavoliResponseDTO;
import it.paoloadesso.gestionaleordini.entities.TavoliEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TavoliMapper {

    CreaTavoliRequestDTO createTavoliEntityToDto(TavoliEntity tavoliEntity);

    @Mapping(target = "id", ignore = true)
    TavoliEntity createTavoliDtoToEntity(CreaTavoliRequestDTO creaTavoliRequestDto);

    TavoliResponseDTO entityToDto(TavoliEntity tavoliEntity);

    @Mapping(target = "id", ignore = true)
    TavoliEntity dtoToEntity(TavoliResponseDTO aggiornaTavoliRequestDto);


}
