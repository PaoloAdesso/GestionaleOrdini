package it.paoloadesso.gestioneordini.repositories;

import it.paoloadesso.gestioneordini.entities.TavoliEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TavoliRepository extends JpaRepository<TavoliEntity, Long> {

    boolean existsByNumeroNomeTavolo(String numeroNomeTavolo);

}
