package com.adm.lucas.posts.adapter.inbound.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(name = "answers")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "comment")
public class AnswerEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private UserEntity user;

    @JoinColumn(name = "comment", nullable = false)
    @ManyToOne
    private CommentEntity comment;

    @Column(nullable = false)
    private LocalDateTime dateAnswer = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));

    @Column(nullable = false)
    private String text;

}