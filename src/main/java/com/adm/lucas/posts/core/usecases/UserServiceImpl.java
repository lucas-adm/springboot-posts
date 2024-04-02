package com.adm.lucas.posts.core.usecases;

import com.adm.lucas.posts.core.domain.User;
import com.adm.lucas.posts.core.ports.repositories.UserRepositoryPort;
import com.adm.lucas.posts.core.ports.services.UserServicePort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserServiceImpl implements UserServicePort {

    private final UserRepositoryPort repositoryPort;

    public UserServiceImpl(UserRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public String login(String username, String password) {
        return repositoryPort.auth(username, password);
    }

    @Override
    public void register(User user) {
        repositoryPort.saveUser(user);
    }

    @Override
    public void edit(UUID uuid, String email, String username, String password, Optional<String> photo, LocalDate birthDate) {
        User user = repositoryPort.findUserById(uuid);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        if (photo.isPresent()) {
            user.setPhoto(photo);
        }
        user.setBirthDate(birthDate);
        repositoryPort.saveUser(user);
    }

    @Override
    public User detail(UUID uuid) {
        return repositoryPort.findUserById(uuid);
    }

    @Override
    public List<User> listAll() {
        return repositoryPort.findAllUsersByActiveTrue();
    }

    @Override
    public void deactivate(UUID uuid) {
        User user = repositoryPort.findUserById(uuid);
        user.setActive(false);
        repositoryPort.saveUser(user);
    }

}