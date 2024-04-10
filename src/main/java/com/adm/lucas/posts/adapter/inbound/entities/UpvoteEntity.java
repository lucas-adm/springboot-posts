package com.adm.lucas.posts.adapter.inbound.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "upvotes")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "post")
public class UpvoteEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private UserEntity user;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne
    private PostEntity post;

    public UpvoteEntity(UserEntity user, PostEntity post) {
        this.user = user;
        this.post = post;
    }

}