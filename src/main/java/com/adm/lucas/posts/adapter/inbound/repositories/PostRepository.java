package com.adm.lucas.posts.adapter.inbound.repositories;

import com.adm.lucas.posts.adapter.inbound.entities.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<PostEntity, UUID> {

    @Query("SELECT p FROM PostEntity p JOIN FETCH p.user")
    Page<PostEntity> findAllPosts(Pageable pageable);

    @Query("SELECT p FROM PostEntity p JOIN FETCH p.user WHERE p.username = :username")
    Page<PostEntity> findPostsByUsername(@Param("username") String username, Pageable pageable);

    @Query("SELECT p FROM PostEntity p JOIN FETCH p.user WHERE p.id = :uuid")
    Optional<PostEntity> findPostById(@Param("uuid") UUID uuid);

}