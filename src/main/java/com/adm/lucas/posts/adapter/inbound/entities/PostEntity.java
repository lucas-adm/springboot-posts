package com.adm.lucas.posts.adapter.inbound.entities;

import com.adm.lucas.posts.core.domain.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
public class PostEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @JoinColumn(name = "id_user", nullable = false)
    @ManyToOne
    private UserEntity user;

    @Column(name = "date_post", nullable = false)
    private LocalDateTime datePost = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.ATIVO;

    @Column(nullable = false)
    private String text;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<UpvoteEntity> upvotes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<CommentEntity> comments;

    @Column(nullable = false)
    private int upvoteCount = 0;

    @Column(nullable = false)
    private int commentCount = 0;

}