package com.adm.lucas.posts.adapter.inbound.controllers;

import com.adm.lucas.posts.core.ports.services.UpvoteServicePort;
import com.auth0.jwt.JWT;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/posts/post/upvotes")
@RequiredArgsConstructor
public class UpvoteController {

    private final UpvoteServicePort servicePort;

    @Transactional
    @PostMapping("/{uuid}")
    public ResponseEntity upvotePost(@RequestHeader("Authorization") String token, @PathVariable UUID uuid) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.upvote(username, uuid);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @DeleteMapping("/{uuid}")
    public void deleteUpvote(@RequestHeader("Authorization") String token, @PathVariable UUID uuid) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.remove(username, uuid);
    }

}