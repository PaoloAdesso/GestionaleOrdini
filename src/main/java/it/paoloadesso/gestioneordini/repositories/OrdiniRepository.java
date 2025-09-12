package it.paoloadesso.gestioneordini.repositories;

import it.paoloadesso.gestioneordini.entities.OrdiniEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdiniRepository extends JpaRepository<OrdiniEntity, Long> {
}
