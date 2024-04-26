package com.adm.lucas.posts.adapter.inbound.controllers;

import com.adm.lucas.posts.adapter.inbound.dtos.in.post.PostDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.post.PostCreatedDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.post.PostDetailDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.post.PostsDTO;
import com.adm.lucas.posts.core.domain.Post;
import com.adm.lucas.posts.core.ports.services.PostServicePort;
import com.auth0.jwt.JWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@SecurityRequirement(name = "bearer-key")
@RequestMapping("/posts")
@Tag(name = "Post Controller", description = "RESTful API for managing posts")
@RequiredArgsConstructor
public class PostController {

    private final PostServicePort servicePort;

    @Operation(summary = "Create a new post")
    @Transactional
    @PostMapping
    public ResponseEntity<PostCreatedDTO> newPost(@Parameter(hidden = true) @RequestHeader("Authorization") String token, @RequestBody @Valid PostDTO dto, UriComponentsBuilder uriComponentsBuilder) {
        Post post = servicePort.create(JWT.decode(token.replace("Bearer ", "")).getSubject(), dto.text());
        var uri = uriComponentsBuilder.path("/posts/post/{id}").buildAndExpand(post.getId()).toUri();
        return ResponseEntity.created(uri).body(new PostCreatedDTO(post));
    }

    @Operation(summary = "Get all posts", description = "Retrieves a page of posts")
    @GetMapping
    public ResponseEntity<List<PostsDTO>> allPosts(@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "datePost") String sortBy, @RequestParam(defaultValue = "desc") String sortOrder) {
        List<Post> posts = servicePort.listPosts(page, size, sortBy, sortOrder);
        return ResponseEntity.ok(posts.stream().map(PostsDTO::new).toList());
    }

    @Operation(summary = "Get all user posts by user username", description = "Search for user username to get his posts")
    @GetMapping("/{username}")
    public ResponseEntity<List<PostsDTO>> allUserPosts(@PathVariable String username, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "datePost") String sortBy, @RequestParam(defaultValue = "desc") String sortOrder) {
        List<Post> posts = servicePort.listUserPosts(username, page, size, sortBy, sortOrder);
        return ResponseEntity.ok(posts.stream().map(PostsDTO::new).toList());
    }

    @Operation(summary = "Select a post by id", description = "Enter to a post to see all details like comments ans answers")
    @GetMapping("/post/{uuid}")
    public ResponseEntity<PostDetailDTO> viewPost(@PathVariable UUID uuid) {
        Post post = servicePort.select(uuid);
        return ResponseEntity.ok(new PostDetailDTO(post));
    }

    @Operation(summary = "Edit your post by id", description = "Change your post text")
    @Transactional
    @PutMapping("/post/{uuid}")
    public ResponseEntity editPost(@Parameter(hidden = true) @RequestHeader("Authorization") String token, @PathVariable UUID uuid, @RequestBody @Valid PostDTO dto) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.edit(uuid, username, dto.text());
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Close your post by id", description = "Closing a post you turn comment and answers unavailable")
    @Transactional
    @PatchMapping("/post/{uuid}")
    public ResponseEntity editPostStatus(@Parameter(hidden = true) @RequestHeader("Authorization") String token, @PathVariable UUID uuid) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.close(uuid, username);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Delete your post by id", description = "Excludes a post from database")
    @Transactional
    @DeleteMapping("/post/{uuid}")
    public ResponseEntity deletePost(@Parameter(hidden = true) @RequestHeader("Authorization") String token, @PathVariable UUID uuid) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.delete(uuid, username);
        return ResponseEntity.noContent().build();
    }

}