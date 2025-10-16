package it.paoloadesso.gestioneordini.repositories;

import it.paoloadesso.gestioneordini.entities.ProdottiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdottiRepository extends JpaRepository<ProdottiEntity, Long> {

    boolean existsByNomeIgnoreCase(String nomeProdotto);

    List<ProdottiEntity> findByNomeContainingIgnoreCase (String nomeProdotto);

    List<ProdottiEntity> findByCategoriaContainingIgnoreCase (String nomeCategoria);

    @Query("SELECT DISTINCT p.categoria FROM ProdottiEntity p ORDER BY p.categoria")
    List<String> findAllCategorieDistinct();
}
