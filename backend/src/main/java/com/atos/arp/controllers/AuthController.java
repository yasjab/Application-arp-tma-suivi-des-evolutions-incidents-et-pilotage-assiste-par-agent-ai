package com.atos.arp.controllers;

import com.atos.arp.models.User;
import com.atos.arp.services.AuthService;
import com.atos.arp.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = authService.authenticate(request.getLogin(), request.getPassword());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generateToken(user.getLogin(), user.getRole().getName());
        LoginResponse res = new LoginResponse(user.getLogin(), user.getRole().getName(), token);
        return ResponseEntity.ok(res);
    }

    public static class LoginRequest {
        private String login;
        private String password;

        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String login;
        private String role;
        private String token;

        public LoginResponse(String login, String role, String token) {
            this.login = login;
            this.role  = role;
            this.token = token;
        }

        public String getLogin() { return login; }
        public String getRole()  { return role; }
        public String getToken() { return token; }
    }
}