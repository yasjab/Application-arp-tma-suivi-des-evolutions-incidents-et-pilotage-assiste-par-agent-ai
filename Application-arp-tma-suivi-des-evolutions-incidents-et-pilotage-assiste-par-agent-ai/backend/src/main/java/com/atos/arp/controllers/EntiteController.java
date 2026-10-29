package com.atos.arp.controllers;

import com.atos.arp.models.Entite;
import com.atos.arp.repositories.EntiteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entites")
public class EntiteController {

    private final EntiteRepository repo;

    public EntiteController(EntiteRepository repo) {
        this.repo = repo;
    }

    // GET /api/entites — accessible par USER et ADMIN
    @GetMapping
    public List<Entite> getAll() { return repo.findAll(); }

    @PostMapping
    public ResponseEntity<Entite> create(@RequestBody Entite entite) {
        if (repo.findByNom(entite.getNom()).isPresent())
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(repo.save(entite));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Entite> update(@PathVariable Long id, @RequestBody Entite body) {
        return repo.findById(id).map(e -> {
            e.setNom(body.getNom());
            return ResponseEntity.ok(repo.save(e));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}