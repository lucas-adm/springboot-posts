package com.adm.lucas.posts.adapter.inbound.repositories;

import com.adm.lucas.posts.adapter.inbound.entities.AnswerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AnswerRepository extends JpaRepository<AnswerEntity, UUID> {

    @Query("SELECT a FROM AnswerEntity a JOIN FETCH a.comment c WHERE c.id = :uuid")
    Page<AnswerEntity> findAllAnswersByComment(@Param("uuid") UUID uuid, Pageable pageable);

}