package com.adm.lucas.posts.core.ports.services;

import com.adm.lucas.posts.core.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface UserServicePort {

    String login(String username, String password);

    void register(User user);

    void activate(UUID uuid);

    void edit(UUID uuid, String username, String newEmail, String newUsername, String newPassword, LocalDate newBirthDate);

    String forgotPassword(String email);

    void changePassword(String email, String password);

    void changePhoto(UUID uuid, String username, String photo);

    List<User> listAll();

    User findByUsername(String username);

    void deactivate(String username, UUID uuid);

    void delete(String username, UUID uuid);

}