package it.paoloadesso.gestionaleordini.repositories;

import it.paoloadesso.gestionaleordini.dto.OrdiniDTO;
import it.paoloadesso.gestionaleordini.entities.OrdiniEntity;
import it.paoloadesso.gestionaleordini.enums.StatoOrdine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrdiniRepository extends JpaRepository<OrdiniEntity, Long> {

    List<OrdiniEntity> findByTavoloId(Long idTavolo);

    List<OrdiniEntity> findByDataOrdine(LocalDate data);

    List<OrdiniEntity> findByTavoloIdAndDataOrdine(Long idTavolo, LocalDate data);

    List<OrdiniEntity> findByTavoloIdAndStatoOrdineNot(Long idTavolo, StatoOrdine stato);

    @Query("SELECT new it.paoloadesso.gestionaleordini.dto.OrdiniDTO(" +
            "o.idOrdine, o.tavolo.id, o.dataOrdine, o.statoOrdine) " +
            "FROM OrdiniEntity o WHERE o.statoOrdine <> :stato")
    List<OrdiniDTO> findOrdiniDtoByStatoOrdineNot(@Param("stato") StatoOrdine stato);


    List<OrdiniEntity> findByStatoOrdineNot(StatoOrdine stato);

    List<OrdiniEntity> findByDataOrdineAndStatoOrdineNot(LocalDate data, StatoOrdine stato);

    List<OrdiniEntity> findByTavoloIdAndDataOrdineAndStatoOrdineNot(Long idTavolo, LocalDate data, StatoOrdine stato);
}
