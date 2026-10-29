package com.atos.arp.config;

import com.atos.arp.models.Entite;
import com.atos.arp.models.Role;
import com.atos.arp.models.User;
import com.atos.arp.repositories.EntiteRepository;
import com.atos.arp.repositories.RoleRepository;
import com.atos.arp.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner init(RoleRepository roleRepo, UserRepository userRepo,
                           PasswordEncoder encoder, EntiteRepository entiteRepo) {
        return args -> {
            // Création des rôles si absents
            if (roleRepo.count() == 0) {
                roleRepo.saveAll(List.of(
                        new Role("ADMIN"),
                        new Role("USER")
                ));
            }

            // Vérification que l'utilisateur admin n'existe pas
            if(userRepo.findByLogin("s.sedrati").isEmpty()) {
                User Admin = new User();
                Admin.setName("Salma");
                Admin.setFirstName("Sedrati");
                Admin.setLogin("s.sedrati");
                Admin.setPassword(encoder.encode("pass"));
                Admin.setEmail("salmasedrati22@gmail.com");
                Admin.setRole(roleRepo.findByName("ADMIN"));
                Admin.setDateCreation(LocalDateTime.now());
                userRepo.save(Admin);
            }
        };
    }
}