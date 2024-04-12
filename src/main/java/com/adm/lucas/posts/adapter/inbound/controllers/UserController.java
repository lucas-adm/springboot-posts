package com.adm.lucas.posts.adapter.inbound.controllers;

import com.adm.lucas.posts.adapter.inbound.dtos.in.user.UserLoginDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.in.user.UserPhotoDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.in.user.UserRegisterDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.in.user.UserUpdateDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.user.UserDetailDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.user.UserTokenDTO;
import com.adm.lucas.posts.core.domain.User;
import com.adm.lucas.posts.core.ports.services.UserServicePort;
import com.auth0.jwt.JWT;
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

    @Transactional
    @PostMapping("/login")
    public ResponseEntity<UserTokenDTO> login(@RequestBody @Valid UserLoginDTO dto) {
        String message = servicePort.login(dto.username(), dto.password());
        return ResponseEntity.accepted().body(new UserTokenDTO(message));
    }

    @GetMapping
    public ResponseEntity<List<UserDetailDTO>> getAllUser() {
        List<User> users = servicePort.listAll();
        List<UserDetailDTO> usersDetails = users.stream().map(UserDetailDTO::new).toList();
        return ResponseEntity.ok().body(usersDetails);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDetailDTO> findUser(@PathVariable String username) {
        User user = servicePort.findByUsername(username);
        UserDetailDTO userDetails = new UserDetailDTO(user);
        return ResponseEntity.ok().body(userDetails);
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
    public ResponseEntity editUser(@RequestHeader("Authorization") String token, @PathVariable UUID uuid, @Valid @RequestBody UserUpdateDTO dto) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.edit(uuid, username, dto.newEmail(), dto.newUsername(), dto.newPassword(), dto.newBirthDate());
        return ResponseEntity.accepted().build();
    }

    @Transactional
    @PatchMapping("/edit/{uuid}")
    public ResponseEntity changePhoto(@RequestHeader("Authorization") String token, @PathVariable UUID uuid, @Valid @RequestBody UserPhotoDTO dto) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.changePhoto(uuid, username, dto.photo());
        return ResponseEntity.accepted().build();
    }

    @Transactional
    @DeleteMapping("/{uuid}")
    public ResponseEntity deleteUser(@RequestHeader("Authorization") String token, @PathVariable UUID uuid) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.delete(username, uuid);
        return ResponseEntity.noContent().build();
    }

}