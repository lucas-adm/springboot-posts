package com.adm.lucas.posts.adapter.inbound.controllers;

import com.adm.lucas.posts.adapter.inbound.dtos.in.comment.CommentDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.comment.CommentCreatedDTO;
import com.adm.lucas.posts.core.domain.Comment;
import com.adm.lucas.posts.core.ports.services.CommentServicePort;
import com.auth0.jwt.JWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
@SecurityRequirement(name = "bearer-key")
@RequestMapping("posts/post")
@Tag(name = "Comment Controller", description = "RESTful API for managing posts comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentServicePort servicePort;

    @Operation(summary = "Insert a comment by post id", description = "Use the Post id to insert a comment")
    @Transactional
    @PostMapping("/{uuid}/comments")
    public ResponseEntity<CommentCreatedDTO> postComment(@RequestHeader("Authorization") String token, @PathVariable UUID uuid, @Valid @RequestBody CommentDTO dto) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        Comment comment = servicePort.comment(username, uuid, dto.text());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommentCreatedDTO(comment));
    }

    @Operation(summary = "Edit your comment by id", description = "Change your comment text")
    @Transactional
    @PatchMapping("/comments/{uuid}")
    public ResponseEntity editComment(@RequestHeader("Authorization") String token, @PathVariable UUID uuid, @Valid @RequestBody CommentDTO dto) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.edit(username, uuid, dto.text());
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Delete your comment by id", description = "Excludes a comment from database")
    @Transactional
    @DeleteMapping("/comments/{uuid}")
    public ResponseEntity deleteComment(@RequestHeader("Authorization") String token, @PathVariable UUID uuid) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.remove(username, uuid);
        return ResponseEntity.noContent().build();
    }

}