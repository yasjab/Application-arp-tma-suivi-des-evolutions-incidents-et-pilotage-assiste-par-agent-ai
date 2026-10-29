package com.atos.arp.services;

import com.atos.arp.models.User;
import com.atos.arp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    // Vérifie le login + mot de passe avec logs
    public User authenticate(String login, String password) {
        System.out.println("Authentification demandée pour login: " + login);

        Optional<User> userOpt = userRepo.findByLogin(login);
        if (userOpt.isEmpty()) {
            System.out.println("Aucun utilisateur trouvé avec ce login !");
            return null;
        }

        User u = userOpt.get();
        System.out.println("Utilisateur trouvé : " + u.getLogin() + ", hash: " + u.getPassword());

        boolean matches = encoder.matches(password, u.getPassword());
        System.out.println("Mot de passe correspond : " + matches);

        if (matches) {
            System.out.println("Authentification réussie pour : " + login);
            return u;
        } else {
            System.out.println("Mot de passe incorrect pour : " + login);
            return null;
        }
    }
}