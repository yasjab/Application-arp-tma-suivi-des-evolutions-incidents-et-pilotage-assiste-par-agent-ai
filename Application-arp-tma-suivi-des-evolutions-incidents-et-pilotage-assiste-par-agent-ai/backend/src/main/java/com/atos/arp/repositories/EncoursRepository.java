package com.atos.arp.repositories;

import com.atos.arp.models.Encours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EncoursRepository extends JpaRepository<Encours, Long> {

    @Query("SELECT e FROM Encours e WHERE e.entite IN :entites")
    List<Encours> findByEntiteIn(@Param("entites") List<String> entites);

    @Query("SELECT e FROM Encours e WHERE e.entite = 'DSI' OR e.entite IS NULL OR e.entite = ''")
    List<Encours> findByEntiteDSI();

    @Query("SELECT e FROM Encours e")
    List<Encours> findAllData();
}