package com.adm.lucas.posts.adapter.inbound.controllers;

import com.adm.lucas.posts.core.ports.services.UpvoteServicePort;
import com.auth0.jwt.JWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
@SecurityRequirement(name = "bearer-key")
@RequestMapping("/posts/post/upvotes")
@Tag(name = "Upvote Controller", description = "RESTful API for managing posts upvotes")
@RequiredArgsConstructor
public class UpvoteController {

    private final UpvoteServicePort servicePort;

    @Operation(summary = "Upvote a post by post id", description = "Increase the number of a Post upvote")
    @Transactional
    @PostMapping("/{uuid}")
    public ResponseEntity upvotePost(@RequestHeader("Authorization") String token, @PathVariable UUID uuid) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.upvote(username, uuid);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove your upvote by post id", description = "Decrease the number of a Post upvote")
    @Transactional
    @DeleteMapping("/{uuid}")
    public void deleteUpvote(@RequestHeader("Authorization") String token, @PathVariable UUID uuid) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.remove(username, uuid);
    }

}