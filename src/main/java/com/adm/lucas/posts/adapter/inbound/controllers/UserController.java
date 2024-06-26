package com.adm.lucas.posts.adapter.inbound.controllers;

import com.adm.lucas.posts.adapter.inbound.dtos.in.user.*;
import com.adm.lucas.posts.adapter.inbound.dtos.out.user.UserDetailDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.user.UserTokenDTO;
import com.adm.lucas.posts.adapter.outbound.producers.UserProducer;
import com.adm.lucas.posts.core.domain.User;
import com.adm.lucas.posts.core.ports.services.UserServicePort;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@SecurityRequirement(name = "bearer-key")
@RequestMapping("/users")
@Tag(name = "User Controller", description = "RESTful API for managing users")
@RequiredArgsConstructor
public class UserController {

    private final UserServicePort servicePort;
    private final UserProducer producer;

    @Operation(summary = "User login", description = "Validates a user credentials. You can use a Demo User already created: username:demo password:3Tres Make sure to apply the generated token in the padlock above.")
    @PostMapping("/login")
    public ResponseEntity<UserTokenDTO> login(@RequestBody @Valid UserLoginDTO dto) {
        String message = servicePort.login(dto.username(), dto.password());
        return ResponseEntity.accepted().body(new UserTokenDTO(message));
    }

    @Operation(summary = "Send a email to recover reset your password")
    @PostMapping("/recover")
    public void recoverAccount(@RequestBody @Valid UserRecoverDTO dto) {
        String token = servicePort.forgotPassword(dto.email());
        producer.publishRecoverEmail(dto.email(), token);
    }

    @Operation(summary = "Reset user password", description = "Use the token sent by email in the parameter.")
    @PatchMapping("/psswrd")
    public ResponseEntity setNewPassword(@RequestHeader("Authorization") String token, @RequestBody @Valid UserSetPassword dto) {
        String email = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.changePassword(email, dto.password());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all activated users")
    @Cacheable(value = "users")
    @GetMapping
    public ResponseEntity<List<UserDetailDTO>> getAllUser() {
        List<User> users = servicePort.listAll();
        List<UserDetailDTO> usersDetails = users.stream().map(UserDetailDTO::new).toList();
        return ResponseEntity.ok().body(usersDetails);
    }

    @Operation(summary = "Get a user by username", description = "Retrieves a user account")
    @Cacheable(value = "user", key = "#username")
    @GetMapping("/{username}")
    public ResponseEntity<UserDetailDTO> findUser(@PathVariable String username) {
        User user = servicePort.findByUsername(username);
        UserDetailDTO userDetails = new UserDetailDTO(user);
        return ResponseEntity.ok().body(userDetails);
    }

    @Operation(summary = "Register a user", description = "Creates a new user account if email or username is available. Feel free to use your real email to get a greetings message 🙃. \n This app has already a demo user to make the login if you want, visit de login endpoint.")
    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody @Valid UserRegisterDTO dto, UriComponentsBuilder uriComponentsBuilder) {
        User user = dto.toUser();
        servicePort.register(user);
        producer.publishMessageEmail(user);
        var uri = uriComponentsBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @Operation(hidden = true)
    @Transactional
    @GetMapping("/activate/{uuid}")
    public ResponseEntity activateUser(@PathVariable UUID uuid) {
        servicePort.activate(uuid);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("https://srs-posts.onrender.com/#/greetings")).build();
    }

    @Operation(summary = "Edit your user by id", description = "Only the owner account can use this method")
    @CacheEvict(value = {"users", "user"}, key = "#username", allEntries = true)
    @Transactional
    @PutMapping("/edit/{uuid}")
    public ResponseEntity editUser(@Parameter(hidden = true) @RequestHeader("Authorization") String token, @PathVariable UUID uuid, @Valid @RequestBody UserUpdateDTO dto) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.edit(uuid, username, dto.newEmail(), dto.newUsername(), dto.newPassword(), dto.newBirthDate());
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Changes your user photo by id", description = "Changes the account image profile")
    @CacheEvict(value = {"users", "user"}, key = "#username", allEntries = true)
    @Transactional
    @PatchMapping("/edit/{uuid}")
    public ResponseEntity changePhoto(@Parameter(hidden = true) @RequestHeader("Authorization") String token, @PathVariable UUID uuid, @Valid @RequestBody UserPhotoDTO dto) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.changePhoto(uuid, username, dto.photo());
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Deactivates your user by id", description = "Turn the user role to DEACTIVATED")
    @CacheEvict(value = {"users", "user"}, key = "#username", allEntries = true)
    @Transactional
    @DeleteMapping("/deactivate/{uuid}")
    public ResponseEntity deactivateUser(@Parameter(hidden = true) @RequestHeader("Authorization") String token, @PathVariable UUID uuid) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.deactivate(username, uuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Removes your user by id", description = "Excludes a user from database")
    @CacheEvict(value = {"users", "user"}, key = "#username", allEntries = true)
    @Transactional
    @DeleteMapping("/{uuid}")
    public ResponseEntity deleteUser(@Parameter(hidden = true) @RequestHeader("Authorization") String token, @PathVariable UUID uuid) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.delete(username, uuid);
        return ResponseEntity.noContent().build();
    }

}