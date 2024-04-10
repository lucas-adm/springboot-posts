package com.adm.lucas.posts.adapter.inbound.repositories;

import com.adm.lucas.posts.adapter.inbound.entities.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {
}