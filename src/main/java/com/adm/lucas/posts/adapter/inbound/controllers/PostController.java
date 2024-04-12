package com.adm.lucas.posts.adapter.inbound.controllers;

import com.adm.lucas.posts.adapter.inbound.dtos.in.post.PostDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.post.PostDetailDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.post.PostsDTO;
import com.adm.lucas.posts.core.domain.Post;
import com.adm.lucas.posts.core.ports.services.PostServicePort;
import com.auth0.jwt.JWT;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostServicePort servicePort;

    @Transactional
    @PostMapping
    public ResponseEntity<String> newPost(@RequestHeader("Authorization") String token, @RequestBody @Valid PostDTO dto) {
        servicePort.create(JWT.decode(token.replace("Bearer ", "")).getSubject(), dto.text());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<PostsDTO>> allPosts(@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "datePost") String sortBy, @RequestParam(defaultValue = "desc") String sortOrder) {
        List<Post> posts = servicePort.listPosts(page, size, sortBy, sortOrder);
        return ResponseEntity.ok(posts.stream().map(PostsDTO::new).toList());
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<PostsDTO>> allUserPosts(@PathVariable String username, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "datePost") String sortBy, @RequestParam(defaultValue = "desc") String sortOrder) {
        List<Post> posts = servicePort.listUserPosts(username, page, size, sortBy, sortOrder);
        return ResponseEntity.ok(posts.stream().map(PostsDTO::new).toList());
    }

    @GetMapping("/post/{uuid}")
    public ResponseEntity<PostDetailDTO> viewPost(@PathVariable UUID uuid) {
        Post post = servicePort.select(uuid);
        return ResponseEntity.ok(new PostDetailDTO(post));
    }

    @Transactional
    @PutMapping("/post/{uuid}")
    public ResponseEntity editPost(@RequestHeader("Authorization") String token, @PathVariable UUID uuid, @RequestBody @Valid PostDTO dto) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.edit(uuid, username, dto.text());
        return ResponseEntity.accepted().build();
    }

    @Transactional
    @PatchMapping("/post/{uuid}")
    public ResponseEntity editPostStatus(@RequestHeader("Authorization") String token, @PathVariable UUID uuid) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.close(uuid, username);
        return ResponseEntity.accepted().build();
    }

    @Transactional
    @DeleteMapping("/post/{uuid}")
    public ResponseEntity deletePost(@RequestHeader("Authorization") String token, @PathVariable UUID uuid) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.delete(uuid, username);
        return ResponseEntity.noContent().build();
    }

}