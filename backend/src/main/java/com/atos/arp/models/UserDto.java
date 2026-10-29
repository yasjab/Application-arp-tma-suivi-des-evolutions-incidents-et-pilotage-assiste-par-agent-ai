package com.atos.arp.models;

import java.time.LocalDateTime;

public class UserDto {
    private Long id;
    private String name;
    private String firstName;
    private String login;
    private String email;
    private String role;
    private LocalDateTime dateCreation;

    public UserDto() {}

    public UserDto(User u) {
        this.id           = u.getId();
        this.name         = u.getName();
        this.firstName    = u.getFirstName();
        this.login        = u.getLogin();
        this.email        = u.getEmail();
        this.role         = u.getRole().getName();
        this.dateCreation = u.getDateCreation();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getFirstName() { return firstName; }
    public String getLogin() { return login; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public LocalDateTime getDateCreation() { return dateCreation; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLogin(String login) { this.login = login; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}