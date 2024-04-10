package com.adm.lucas.posts.adapter.inbound.repositories;

import com.adm.lucas.posts.adapter.inbound.entities.PostEntity;
import com.adm.lucas.posts.adapter.inbound.entities.UpvoteEntity;
import com.adm.lucas.posts.adapter.inbound.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UpvoteRepository extends JpaRepository<UpvoteEntity, UUID> {
    Optional<UpvoteEntity> findByUserAndPost(UserEntity user, PostEntity post);
}