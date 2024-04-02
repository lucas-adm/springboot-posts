package com.adm.lucas.posts.adapter.inbound.controllers;

import com.adm.lucas.posts.adapter.inbound.dtos.in.UserLoginDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.in.UserRegisterDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.in.UserUpdateDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.UserTokenDTO;
import com.adm.lucas.posts.core.domain.User;
import com.adm.lucas.posts.core.ports.services.UserServicePort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserServicePort servicePort;

    @PostMapping("login")
    public ResponseEntity<UserTokenDTO> login(@RequestBody UserLoginDTO dto) {
        String message = servicePort.login(dto.username(), dto.password());
        return ResponseEntity.accepted().body(new UserTokenDTO(message));
    }

    @GetMapping("{uuid}")
    public User getUser(@PathVariable UUID uuid) {
        return servicePort.detail(uuid);
    }

    @GetMapping
    public List<User> getAllUser() {
        return servicePort.listAll();
    }

    @Transactional
    @PostMapping("register")
    public ResponseEntity<User> registerUser(@RequestBody UserRegisterDTO dto, UriComponentsBuilder uriComponentsBuilder) {
        User user = new User(dto.email(), dto.username(), dto.password(), dto.photo(), dto.birthDate());
        servicePort.register(user);
        var uri = uriComponentsBuilder.path("users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @Transactional
    @PutMapping("edit/{uuid}")
    public ResponseEntity editUser(@PathVariable UUID uuid, @RequestBody UserUpdateDTO dto) {
        servicePort.edit(uuid, dto.email(), dto.username(), dto.password(), dto.photo(), dto.birthDate());
        return ResponseEntity.accepted().build();
    }

    @Transactional
    @DeleteMapping("{uuid}")
    public ResponseEntity deleteUser(@PathVariable UUID uuid) {
        servicePort.deactivate(uuid);
        return ResponseEntity.noContent().build();
    }

}