package com.atos.arp.repositories;

import com.atos.arp.models.FcLivrer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FcLivrerRepository extends JpaRepository<FcLivrer, Long> {

    // Filtre par liste d'entités exactes
    @Query("SELECT f FROM FcLivrer f WHERE f.entite IN :entites")
    List<FcLivrer> findByEntiteIn(@Param("entites") List<String> entites);

    // Filtre DSI (entite = 'DSI' ou entite vide)
    @Query("SELECT f FROM FcLivrer f WHERE f.entite = 'DSI' OR f.entite IS NULL OR f.entite = ''")
    List<FcLivrer> findByEntiteDSI();

    // Tout récupérer (bouton ALL)
    @Query("SELECT f FROM FcLivrer f")
    List<FcLivrer> findAllData();
}