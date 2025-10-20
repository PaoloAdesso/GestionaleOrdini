package it.paoloadesso.gestionaleordini.repositories;

import it.paoloadesso.gestionaleordini.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestionaleordini.entities.keys.OrdiniProdottiId;
import it.paoloadesso.gestionaleordini.enums.StatoOrdine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrdiniProdottiRepository extends JpaRepository<OrdiniProdottiEntity, OrdiniProdottiId> {

    List<OrdiniProdottiEntity> findByOrdineTavoloId(Long idTavolo);

    List<OrdiniProdottiEntity> findByOrdineTavoloIdAndOrdineStatoOrdineNot(Long idTavolo, StatoOrdine stato);

    List<OrdiniProdottiEntity> findByOrdineTavoloIdAndOrdineDataOrdineAndOrdineStatoOrdineNot(Long idTavolo, LocalDate data, StatoOrdine stato);

    List<OrdiniProdottiEntity> findByOrdineIdOrdine(Long idOrdine);
}
