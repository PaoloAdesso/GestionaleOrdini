package it.paoloadesso.gestioneordini.repositories;

import it.paoloadesso.gestioneordini.entities.ProdottiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdottiRepository extends JpaRepository<ProdottiEntity, Long> {
}
