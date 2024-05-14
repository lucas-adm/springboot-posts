package com.adm.lucas.posts.adapter.inbound.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
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

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<AnswerEntity> answers;

    @Column(nullable = false)
    private int answerCount = 0;

}