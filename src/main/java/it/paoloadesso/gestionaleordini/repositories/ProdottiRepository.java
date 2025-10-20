package it.paoloadesso.gestionaleordini.repositories;

import it.paoloadesso.gestionaleordini.entities.ProdottiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdottiRepository extends JpaRepository<ProdottiEntity, Long> {

    boolean existsByNomeIgnoreCase(String nomeProdotto);

    List<ProdottiEntity> findByNomeContainingIgnoreCase (String nomeProdotto);

    List<ProdottiEntity> findByCategoriaContainingIgnoreCase (String nomeCategoria);

    @Query("SELECT DISTINCT p.categoria FROM ProdottiEntity p ORDER BY p.categoria")
    List<String> findAllCategorieDistinct();
}
