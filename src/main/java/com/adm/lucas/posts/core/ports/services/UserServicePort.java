package com.adm.lucas.posts.core.ports.services;

import com.adm.lucas.posts.core.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserServicePort {

    String login(String username, String password);

    void register(User user);

    void edit(UUID uuid, String email, String username, String password, Optional<String> photo, LocalDate birthDate);

    User detail(UUID uuid);

    List<User> listAll();

    void deactivate(UUID uuid);

}