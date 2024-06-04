package com.adm.lucas.posts.adapter.inbound.repositories;

import com.adm.lucas.posts.adapter.inbound.entities.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface PostRepository extends JpaRepository<PostEntity, UUID> {

    @Query("SELECT DISTINCT p FROM PostEntity p JOIN FETCH p.user LEFT JOIN FETCH p.comments LEFT JOIN FETCH p.upvotes")
    Page<PostEntity> findAllPosts(Pageable pageable);

    Page<PostEntity> findByUserUsername(String username, Pageable pageable);

}