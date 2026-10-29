package com.atos.arp.repositories;

import com.atos.arp.models.DiLivrer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DiLivrerRepository extends JpaRepository<DiLivrer, Long> {

    @Query("SELECT d FROM DiLivrer d WHERE d.entite IN :entites")
    List<DiLivrer> findByEntiteIn(@Param("entites") List<String> entites);

    @Query("SELECT d FROM DiLivrer d WHERE d.entite = 'DSI' OR d.entite IS NULL OR d.entite = ''")
    List<DiLivrer> findByEntiteDSI();

    @Query("SELECT d FROM DiLivrer d")
    List<DiLivrer> findAllData();
}