package com.adm.lucas.posts.adapter.inbound.usecases;

import com.adm.lucas.posts.adapter.inbound.entities.UserEntity;
import com.adm.lucas.posts.adapter.inbound.repositories.UserRepository;
import com.adm.lucas.posts.core.domain.User;
import com.adm.lucas.posts.core.ports.repositories.UserRepositoryPort;
import com.adm.lucas.posts.infra.security.TokenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository repository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public void saveUser(User user) {
        Optional<UserEntity> optionalEntity = repository.findByUsernameOrEmail(user.getUsername(), user.getEmail());
        if (optionalEntity.isPresent()) {
            UserEntity entity = optionalEntity.get();
            if (entity.getId() != user.getId()) {
                throw new RuntimeException("Email or username are unavailable.");
            }
        }
        UserEntity entity = modelMapper.map(user, UserEntity.class);
        int age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();
        if (age < 12) {
            throw new RuntimeException("User must be at least 12 years old.");
        }
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        repository.save(entity);
    }

    @Override
    public User findByUserUsername(String username) {
        UserEntity entity = repository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(entity, User.class);
    }

    @Override
    public User findByUserUsernameOrEmail(String username, String email) {
        UserEntity entity = repository.findByUsernameOrEmail(username, email).orElse(null);
        return modelMapper.map(entity, User.class);
    }

    @Override
    public User findUserById(UUID uuid) {
        UserEntity entity = repository.findById(uuid).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(entity, User.class);
    }

    @Override
    public List<User> findAllUsersByActiveTrue() {
        List<UserEntity> entities = repository.findAllByActiveTrue();
        List<User> users = new ArrayList<>();
        for (UserEntity entity : entities) {
            users.add(modelMapper.map(entity, User.class));
        }
        return users;
    }

    @Override
    public String auth(String username, String password) {
        UserEntity entity = repository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        boolean matches = passwordEncoder.matches(password, entity.getPassword());
        if (!matches) {
            throw new RuntimeException("Invalid password.");
        }
        return tokenService.generateToken(entity);
    }

}