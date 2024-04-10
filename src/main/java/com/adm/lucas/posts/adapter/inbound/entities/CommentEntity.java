package com.adm.lucas.posts.adapter.inbound.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Data
@EqualsAndHashCode(exclude = "post")
public class CommentEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private UserEntity user;

    @JoinColumn(name = "post", nullable = false)
    @ManyToOne
    private PostEntity post;

    @Column(nullable = false)
    private String text;

    @Column(name = "date_comment", nullable = false)
    private LocalDateTime dateComment = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));

}