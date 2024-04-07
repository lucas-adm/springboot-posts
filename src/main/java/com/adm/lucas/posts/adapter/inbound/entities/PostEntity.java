package com.adm.lucas.posts.adapter.inbound.entities;

import com.adm.lucas.posts.core.domain.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Data
public class PostEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Column(nullable = false)
    private String username;

    @Column(name = "date_post", nullable = false)
    private LocalDateTime datePost = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.OPEN;

    @Column(nullable = false)
    private String text;

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CommentEntity> comments = new ArrayList<>();

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<UpvoteEntity> upvotes = new ArrayList<>();

}