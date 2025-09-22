package it.paoloadesso.gestioneordini.mapper;

import it.paoloadesso.gestioneordini.dto.CreaProdottiDto;
import it.paoloadesso.gestioneordini.dto.ProdottiDto;
import it.paoloadesso.gestioneordini.entities.ProdottiEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProdottiMapper {

    @Mapping(source = "id", target = "idProdotto")
    ProdottiDto createProdottiEntityToDto (ProdottiEntity prodottiEntity);

    @Mapping(target = "id", ignore = true)
    ProdottiEntity createProdottiDtoToEntity (CreaProdottiDto prodottiDto);
}
