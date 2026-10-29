package com.atos.arp.controllers;

import com.atos.arp.models.User;
import com.atos.arp.models.UserDto;
import com.atos.arp.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public ProfileController(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder  = encoder;
    }

    // GET /api/profile — récupérer son propre profil
    @GetMapping
    public ResponseEntity<UserDto> getProfile() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> opt = userRepo.findByLogin(login);
        return opt.map(u -> ResponseEntity.ok(new UserDto(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/profile/password — modifier mot de passe
    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@RequestBody PasswordRequest req) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> opt = userRepo.findByLogin(login);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        User u = opt.get();

        if (!encoder.matches(req.getCurrentPassword(), u.getPassword()))
            return ResponseEntity.status(400).build();

        if (req.getNewPassword() == null || req.getNewPassword().length() < 6)
            return ResponseEntity.status(400).build();

        u.setPassword(encoder.encode(req.getNewPassword()));
        userRepo.save(u);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/infos")
    public ResponseEntity<UserDto> updateInfos(@RequestBody InfosRequest req) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> opt = userRepo.findByLogin(login);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        User u = opt.get();

        // Vérifier que le nouveau login n'est pas déjà pris par quelqu'un d'autre
        if (!req.getLogin().equals(u.getLogin())) {
            Optional<User> existing = userRepo.findByLogin(req.getLogin());
            if (existing.isPresent()) return ResponseEntity.status(409).build(); // conflit
        }

        u.setName(req.getName());
        u.setFirstName(req.getFirstName());
        u.setEmail(req.getEmail());
        u.setLogin(req.getLogin());

        return ResponseEntity.ok(new UserDto(userRepo.save(u)));
    }

    public static class InfosRequest {
        private String name;
        private String firstName;
        private String email;
        private String login; // ← ajouté

        public String getName() { return name; }
        public String getFirstName() { return firstName; }
        public String getEmail() { return email; }
        public String getLogin() { return login; } // ← ajouté

        public void setName(String name) { this.name = name; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public void setEmail(String email) { this.email = email; }
        public void setLogin(String login) { this.login = login; } // ← ajouté
    }

    public static class PasswordRequest {
        private String currentPassword;
        private String newPassword;

        public String getCurrentPassword() { return currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}