package com.adm.lucas.posts.core.ports.repositories;

import com.adm.lucas.posts.core.domain.User;

import java.util.List;
import java.util.UUID;

public interface UserRepositoryPort {

    void saveUser(User user);

    User findByUserUsername(String username);

    User findByUserUsernameOrEmail(String username, String email);

    User findUserById(UUID uuid);

    List<User> findAllUsersByActiveTrue();

    String auth(String username, String password);

}