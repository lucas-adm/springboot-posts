package com.adm.lucas.posts.core.usecases;

import com.adm.lucas.posts.core.domain.Role;
import com.adm.lucas.posts.core.domain.User;
import com.adm.lucas.posts.core.ports.repositories.UserRepositoryPort;
import com.adm.lucas.posts.core.ports.services.UserServicePort;

import java.time.LocalDate;
import java.util.*;

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
        repositoryPort.registerUser(user);
    }

    @Override
    public void activate(UUID uuid) {
        User user = repositoryPort.findUserById(uuid);
        user.setRole(Role.ACTIVATED);
        repositoryPort.saveUser(user);
    }

    @Override
    public void edit(UUID uuid, String username, String newEmail, String newUsername, String newPassword, LocalDate newBirthDate) {
        User user = repositoryPort.findUserById(uuid);
        if (!Objects.equals(user.getUsername(), username)) {
            throw new RuntimeException("Apenas o próprio criador pode editar esta conta.");
        }
        user.setEmail(newEmail.toLowerCase());
        user.setUsername(newUsername);
        user.setPassword(newPassword);
        user.setBirthDate(newBirthDate);
        repositoryPort.registerUser(user);
    }

    @Override
    public String forgotPassword(String email) {
        return repositoryPort.sendToken(email);
    }

    @Override
    public void changePassword(String email, String password) {
        User user = repositoryPort.findUserByEmail(email);
        user.setPassword(password);
        repositoryPort.registerUser(user);
    }

    @Override
    public void changePhoto(UUID uuid, String username, String photo) {
        User user = repositoryPort.findUserById(uuid);
        if (!Objects.equals(user.getUsername(), username)) {
            throw new RuntimeException("Apenas o próprio criador pode editar esta conta.");
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
    public void deactivate(String username, UUID uuid) {
        User user = repositoryPort.findUserById(uuid);
        if (!Objects.equals(user.getUsername(), username)) {
            throw new RuntimeException("Apenas o próprio criador pode editar esta conta.");
        }
        int randomNumber = new Random().nextInt(100000);
        user.setEmail(user.getEmail() + randomNumber);
        user.setRole(Role.DEACTIVATED);
        repositoryPort.saveUser(user);
    }

    @Override
    public void delete(String username, UUID uuid) {
        User user = repositoryPort.findUserById(uuid);
        if (!Objects.equals(user.getUsername(), username)) {
            throw new RuntimeException("Apenas o próprio criador pode editar esta conta.");
        }
        repositoryPort.deleteUser(user);
    }

}