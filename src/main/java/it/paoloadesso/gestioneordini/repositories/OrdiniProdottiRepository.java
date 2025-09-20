package it.paoloadesso.gestioneordini.repositories;

import it.paoloadesso.gestioneordini.entities.OrdiniEntity;
import it.paoloadesso.gestioneordini.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestioneordini.entities.keys.OrdiniProdottiId;
import it.paoloadesso.gestioneordini.enums.StatoOrdine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdiniProdottiRepository extends JpaRepository<OrdiniProdottiEntity, OrdiniProdottiId> {

    List<OrdiniProdottiEntity> findByOrdine_Tavolo_Id(Long idTavolo);

    List<OrdiniProdottiEntity> findByOrdine_Tavolo_IdAndOrdine_StatoOrdineNot(Long idTavolo, StatoOrdine stato);

}
