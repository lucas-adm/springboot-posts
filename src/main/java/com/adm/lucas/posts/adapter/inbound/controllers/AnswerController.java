package com.adm.lucas.posts.adapter.inbound.controllers;

import com.adm.lucas.posts.adapter.inbound.dtos.in.answer.AnswerDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.answer.AnswerDetailDTO;
import com.adm.lucas.posts.core.domain.Answer;
import com.adm.lucas.posts.core.ports.services.AnswerServicePort;
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

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@SecurityRequirement(name = "bearer-key")
@RequestMapping("posts/post/comments")
@Tag(name = "Answer Controller", description = "RESTful API for managing comments answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerServicePort servicePort;

    @Operation(summary = "Insert a answer by comment id", description = "Make as many answers as you want")
    @Transactional
    @PostMapping("/{uuid}")
    public ResponseEntity postAnswer(@RequestHeader("Authorization") String token, @PathVariable UUID uuid, @RequestBody @Valid AnswerDTO dto) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.answer(username, uuid, dto.text());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Edit your answer by id", description = "Change your answer text")
    @Transactional
    @PatchMapping("/answers/{uuid}")
    public ResponseEntity editAnswer(@RequestHeader("Authorization") String token, @PathVariable UUID uuid, @RequestBody @Valid AnswerDTO dto) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.edit(username, uuid, dto.text());
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Delete your answer by id", description = "Exclude a answer from database")
    @Transactional
    @DeleteMapping("/answers/{uuid}")
    public ResponseEntity deleteAnswer(@RequestHeader("Authorization") String token, @PathVariable UUID uuid) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.remove(username, uuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all answers by comment id", description = "Retrieves a page of comments")
    @GetMapping("/{uuid}")
    public List<AnswerDetailDTO> getAnswers(@PathVariable UUID uuid, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "dateAnswer") String sortBy, @RequestParam(defaultValue = "desc") String sortOrder) {
        List<Answer> answers = servicePort.listCommentAnswers(uuid, page, size, sortBy, sortOrder);
        return answers.stream().map(AnswerDetailDTO::new).toList();
    }

}