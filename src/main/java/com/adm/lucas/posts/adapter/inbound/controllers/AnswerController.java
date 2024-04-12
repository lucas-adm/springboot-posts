package com.adm.lucas.posts.adapter.inbound.controllers;

import com.adm.lucas.posts.adapter.inbound.dtos.in.answer.AnswerDTO;
import com.adm.lucas.posts.adapter.inbound.dtos.out.answer.AnswerDetailDTO;
import com.adm.lucas.posts.core.domain.Answer;
import com.adm.lucas.posts.core.ports.services.AnswerServicePort;
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
@RequestMapping("posts/post/comments")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerServicePort servicePort;

    @Transactional
    @PostMapping("/{uuid}")
    public ResponseEntity postAnswer(@RequestHeader("Authorization") String token, @PathVariable UUID uuid, @RequestBody @Valid AnswerDTO dto) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.answer(username, uuid, dto.text());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Transactional
    @PatchMapping("/answers/{uuid}")
    public ResponseEntity editAnswer(@RequestHeader("Authorization") String token, @PathVariable UUID uuid, @RequestBody @Valid AnswerDTO dto) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.edit(username, uuid, dto.text());
        return ResponseEntity.accepted().build();
    }

    @Transactional
    @DeleteMapping("/answers/{uuid}")
    public ResponseEntity deleteAnswer(@RequestHeader("Authorization") String token, @PathVariable UUID uuid) {
        String username = JWT.decode(token.replace("Bearer ", "")).getSubject();
        servicePort.remove(username, uuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{uuid}")
    public List<AnswerDetailDTO> getAnswers(@PathVariable UUID uuid, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "dateAnswer") String sortBy, @RequestParam(defaultValue = "desc") String sortOrder) {
        List<Answer> answers = servicePort.listCommentAnswers(uuid, page, size, sortBy, sortOrder);
        return answers.stream().map(AnswerDetailDTO::new).toList();
    }

}