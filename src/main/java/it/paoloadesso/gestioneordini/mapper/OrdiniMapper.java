package it.paoloadesso.gestioneordini.mapper;

import it.paoloadesso.gestioneordini.dto.CreaOrdiniDto;
import it.paoloadesso.gestioneordini.dto.OrdiniDto;
import it.paoloadesso.gestioneordini.entities.OrdiniEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrdiniMapper {

    CreaOrdiniDto createOrdiniEntityToDto (OrdiniEntity ordiniEntity);

    @Mapping(target = "idOrdine", ignore = true)
    OrdiniEntity createOrdiniDtoToEntity (CreaOrdiniDto creaOrdiniDto);

    OrdiniDto ordiniEntityToDto (OrdiniEntity ordiniEntity);

    @Mapping(target = "idOrdine", ignore = true)
    OrdiniEntity ordiniDtoToEntity (OrdiniDto ordiniDto);



}
