package it.paoloadesso.gestioneordini.mapper;

import it.paoloadesso.gestioneordini.dto.CreaOrdiniDto;
import it.paoloadesso.gestioneordini.dto.ListaOrdiniEProdottiByTavoloResponseDto;
import it.paoloadesso.gestioneordini.dto.OrdiniDto;
import it.paoloadesso.gestioneordini.dto.ProdottiOrdinatiResponseDto;
import it.paoloadesso.gestioneordini.entities.OrdiniEntity;
import it.paoloadesso.gestioneordini.entities.OrdiniProdottiEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrdiniMapper {

    CreaOrdiniDto createOrdiniEntityToDto (OrdiniEntity ordiniEntity);

    @Mapping(target = "idOrdine", ignore = true)
    OrdiniEntity createOrdiniDtoToEntity (CreaOrdiniDto creaOrdiniDto);

    @Mapping(target = "idTavolo", source = "tavolo.id")
    OrdiniDto ordiniEntityToDto (OrdiniEntity ordiniEntity);

    @Mapping(target = "idOrdine", source = "ordine.idOrdine")
    @Mapping(target = "idTavolo", source = "ordine.tavolo.id")
    @Mapping(target = "dataOrdine", source = "ordine.dataOrdine")
    @Mapping(target = "statoOrdine", source = "ordine.statoOrdine")
    @Mapping(target = "listaOrdineERelativiProdotti", ignore = true) // Perchè la popolo nel service
    ListaOrdiniEProdottiByTavoloResponseDto ordiniProdottiEntityToDto(OrdiniProdottiEntity ordiniProdottiEntity);

    @Mapping(target = "idOrdine", ignore = true)
    OrdiniEntity ordiniDtoToEntity (OrdiniDto ordiniDto);
}
