package com.adm.lucas.posts.adapter.inbound.controllers;

import com.adm.lucas.posts.adapter.inbound.dtos.in.comment.CommentDTO;
import com.adm.lucas.posts.core.ports.services.CommentServicePort;
import com.auth0.jwt.JWT;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentController {

    private final CommentServicePort servicePort;

    @Transactional
    @PostMapping("posts/post/{uuid}/comments")
    public ResponseEntity postComment(@RequestHeader("Authorization") String token, @PathVariable UUID uuid, @Valid @RequestBody CommentDTO dto) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.comment(username, uuid, dto.text());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Transactional
    @PatchMapping("posts/post/comments/{uuid}")
    public ResponseEntity editComment(@RequestHeader("Authorization") String token, @PathVariable UUID uuid, @Valid @RequestBody CommentDTO dto) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.edit(username, uuid, dto.text());
        return ResponseEntity.accepted().build();
    }

    @Transactional
    @DeleteMapping("posts/post/comments/{uuid}")
    public ResponseEntity deleteComment(@RequestHeader("Authorization") String token, @PathVariable UUID uuid) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.remove(username, uuid);
        return ResponseEntity.noContent().build();
    }

}