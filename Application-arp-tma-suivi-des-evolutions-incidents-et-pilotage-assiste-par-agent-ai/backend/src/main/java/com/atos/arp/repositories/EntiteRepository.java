package com.atos.arp.repositories;

import com.atos.arp.models.Entite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntiteRepository extends JpaRepository<Entite, Long> {
    Optional<Entite> findByNom(String nom);
}