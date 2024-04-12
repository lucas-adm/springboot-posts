package com.adm.lucas.posts.core.usecases;

import com.adm.lucas.posts.core.domain.Role;
import com.adm.lucas.posts.core.domain.User;
import com.adm.lucas.posts.core.ports.repositories.UserRepositoryPort;
import com.adm.lucas.posts.core.ports.services.UserServicePort;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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
    public void edit(UUID uuid, String username, String newEmail, String newUsername, String newPassword, LocalDate newBirthDate) {
        User user = repositoryPort.findUserById(uuid);
        if (!Objects.equals(user.getUsername(), username)) {
            throw new RuntimeException("Only the user owner can edit this account.");
        }
        user.setEmail(newEmail);
        user.setUsername(newUsername);
        user.setPassword(newPassword);
        user.setBirthDate(newBirthDate);
        repositoryPort.saveUser(user);
    }

    @Override
    public void changePhoto(UUID uuid, String username, String photo) {
        User user = repositoryPort.findUserById(uuid);
        if (!Objects.equals(user.getUsername(), username)) {
            throw new RuntimeException("Only the user owner can edit this account.");
        }
        user.setPhoto(Optional.ofNullable(photo));
        repositoryPort.saveUser(user);
    }

    @Override
    public List<User> listAll() {
        return repositoryPort.findAllByRoleActivated();
    }

    @Override
    public User findByUsername(String username) {
        return repositoryPort.findByUserUsername(username);
    }

    @Override
    public void deactivate(UUID uuid) {
        User user = repositoryPort.findUserById(uuid);
        user.setRole(Role.DEACTIVATED);
        repositoryPort.saveUser(user);
    }

    @Override
    public void delete(String username, UUID uuid) {
        User user = repositoryPort.findUserById(uuid);
        if (!Objects.equals(user.getUsername(), username)) {
            throw new RuntimeException("Only the user owner can edit this account.");
        }
        repositoryPort.deleteUser(user);
    }

}