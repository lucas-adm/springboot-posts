package com.adm.lucas.posts.adapter.inbound.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "upvotes")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "post")
public class UpvoteEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @JoinColumn(name = "id_user", nullable = false)
    @ManyToOne
    private UserEntity user;

    @JoinColumn(name = "id_post", nullable = false)
    @ManyToOne
    private PostEntity post;

}