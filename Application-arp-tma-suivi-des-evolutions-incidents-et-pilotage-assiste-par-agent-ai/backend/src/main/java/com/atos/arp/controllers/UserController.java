package com.atos.arp.controllers;

import com.atos.arp.models.Role;
import com.atos.arp.models.User;
import com.atos.arp.models.UserDto;
import com.atos.arp.repositories.RoleRepository;
import com.atos.arp.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    public UserController(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder  = encoder;
    }

    // GET /api/users
    @GetMapping
    public List<UserDto> getAll() {
        return userRepo.findAll().stream().map(UserDto::new).toList();
    }

    // POST /api/users
    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserRequest req) {
        if (userRepo.findByLogin(req.getLogin()).isPresent())
            return ResponseEntity.badRequest().build();

        Role role = roleRepo.findByName(req.getRole());
        if (role == null) return ResponseEntity.badRequest().build();

        User u = new User();
        u.setName(req.getName());
        u.setFirstName(req.getFirstName());
        u.setLogin(req.getLogin());
        u.setPassword(encoder.encode(req.getPassword()));
        u.setEmail(req.getEmail());
        u.setRole(role);
        u.setDateCreation(LocalDateTime.now());

        return ResponseEntity.ok(new UserDto(userRepo.save(u)));
    }

    // PUT /api/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id,
                                          @RequestBody UserRequest req) {
        Optional<User> optional = userRepo.findById(id);
        if (optional.isEmpty()) return ResponseEntity.notFound().build();

        User u = optional.get();
        Role role = roleRepo.findByName(req.getRole());
        if (role == null) return ResponseEntity.badRequest().build();

        u.setName(req.getName());
        u.setFirstName(req.getFirstName());
        u.setEmail(req.getEmail());
        u.setRole(role);

        return ResponseEntity.ok(new UserDto(userRepo.save(u)));
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!userRepo.existsById(id)) return ResponseEntity.notFound().build();
        userRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public static class UserRequest {
        private String name;
        private String firstName;
        private String login;
        private String password;
        private String email;
        private String role;

        public String getName() { return name; }
        public String getFirstName() { return firstName; }
        public String getLogin() { return login; }
        public String getPassword() { return password; }
        public String getEmail() { return email; }
        public String getRole() { return role; }

        public void setName(String name) { this.name = name; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public void setLogin(String login) { this.login = login; }
        public void setPassword(String password) { this.password = password; }
        public void setEmail(String email) { this.email = email; }
        public void setRole(String role) { this.role = role; }
    }
}