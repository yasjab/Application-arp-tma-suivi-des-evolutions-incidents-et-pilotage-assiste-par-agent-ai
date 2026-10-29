package com.atos.arp.repositories;

import com.atos.arp.models.FspAnnulees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FspAnnuleesRepository extends JpaRepository<FspAnnulees, Long> {

    @Query("SELECT f FROM FspAnnulees f WHERE f.entite IN :entites")
    List<FspAnnulees> findByEntiteIn(@Param("entites") List<String> entites);

    @Query("SELECT f FROM FspAnnulees f WHERE f.entite = 'DSI' OR f.entite IS NULL OR f.entite = ''")
    List<FspAnnulees> findByEntiteDSI();

    @Query("SELECT f FROM FspAnnulees f")
    List<FspAnnulees> findAllData();
}