package it.paoloadesso.gestioneordini.repositories;

import it.paoloadesso.gestioneordini.entities.TavoliEntity;
import it.paoloadesso.gestioneordini.enums.StatoTavolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TavoliRepository extends JpaRepository<TavoliEntity, Long> {

    boolean existsByNumeroNomeTavolo(String numeroNomeTavolo);

    List<TavoliEntity> findByStatoTavolo (StatoTavolo statoTavolo);
}
