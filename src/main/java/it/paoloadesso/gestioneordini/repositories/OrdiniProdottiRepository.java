package it.paoloadesso.gestioneordini.repositories;

import it.paoloadesso.gestioneordini.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestioneordini.entities.keys.OrdiniProdottiId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdiniProdottiRepository extends JpaRepository<OrdiniProdottiEntity, OrdiniProdottiId> {
}
