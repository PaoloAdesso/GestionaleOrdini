package it.paoloadesso.gestioneordini.repositories;

import it.paoloadesso.gestioneordini.entities.OrdiniEntity;
import it.paoloadesso.gestioneordini.enums.StatoOrdine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdiniRepository extends JpaRepository<OrdiniEntity, Long> {

    List<OrdiniEntity> findByTavoloId(Long idTavolo);

    List<OrdiniEntity> findByTavoloIdAndStatoOrdineNot(Long idTavolo, StatoOrdine stato);

    List<OrdiniEntity> findByStatoOrdineNot(StatoOrdine stato);


}
