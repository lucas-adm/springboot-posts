package com.adm.lucas.posts.core.domain;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class User {

    private UUID id = UUID.randomUUID();
    private String email;
    private String username;
    private String password;
    private Optional<String> photo;
    private LocalDate birthDate;
    private Role role = Role.ACTIVATED;

    public User() {
    }

    public User(String email, String username, String password, String photo, LocalDate birthDate) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.photo = Optional.ofNullable(photo);
        this.birthDate = birthDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Optional<String> getPhoto() {
        return photo;
    }

    public void setPhoto(Optional<String> photo) {
        this.photo = photo;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}