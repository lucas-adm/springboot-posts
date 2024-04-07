package com.adm.lucas.posts.adapter.inbound.controllers;

import com.adm.lucas.posts.adapter.inbound.dtos.in.user.UserLoginDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.in.user.UserRegisterDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.in.user.UserUpdateDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.user.UserDetailDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.user.UserTokenDTO;
import com.adm.lucas.posts.core.domain.User;
import com.adm.lucas.posts.core.ports.services.UserServicePort;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServicePort servicePort;

    @PostMapping("/login")
    public ResponseEntity<UserTokenDTO> login(@RequestBody @Valid UserLoginDTO dto) {
        String message = servicePort.login(dto.username(), dto.password());
        return ResponseEntity.accepted().body(new UserTokenDTO(message));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<UserDetailDTO> getUser(@PathVariable UUID uuid) {
        User user = servicePort.detail(uuid);
        return ResponseEntity.ok().body(new UserDetailDTO(user.getEmail(), user.getUsername(), user.getPhoto()));
    }

    @GetMapping
    public ResponseEntity<List<UserDetailDTO>> getAllUser() {
        List<User> users = servicePort.listAll();
        List<UserDetailDTO> usersDetails = users.stream()
                .map(user -> new UserDetailDTO(user.getEmail(), user.getUsername(), user.getPhoto())).toList();
        return ResponseEntity.ok().body(usersDetails);
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody @Valid UserRegisterDTO dto, UriComponentsBuilder uriComponentsBuilder) {
        User user = dto.toUser();
        servicePort.register(user);
        var uri = uriComponentsBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @Transactional
    @PutMapping("/edit/{uuid}")
    public ResponseEntity editUser(@PathVariable UUID uuid, @Valid @RequestBody UserUpdateDTO dto) {
        servicePort.edit(uuid, dto.email(), dto.username(), dto.password(), dto.photo(), dto.birthDate());
        return ResponseEntity.accepted().build();
    }

    @Transactional
    @DeleteMapping("/{uuid}")
    public ResponseEntity deleteUser(@PathVariable UUID uuid) {
        servicePort.deactivate(uuid);
        return ResponseEntity.noContent().build();
    }

}