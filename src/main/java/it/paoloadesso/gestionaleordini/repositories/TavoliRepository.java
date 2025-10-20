package it.paoloadesso.gestionaleordini.repositories;

import it.paoloadesso.gestionaleordini.entities.TavoliEntity;
import it.paoloadesso.gestionaleordini.enums.StatoTavolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TavoliRepository extends JpaRepository<TavoliEntity, Long> {

    boolean existsByNumeroNomeTavoloIgnoreCase(String numeroNomeTavolo);

    List<TavoliEntity> findByStatoTavolo (StatoTavolo statoTavolo);

    boolean existsByNumeroNomeTavoloIgnoreCaseAndIdNot(String numeroNomeTavolo, Long id);
}
