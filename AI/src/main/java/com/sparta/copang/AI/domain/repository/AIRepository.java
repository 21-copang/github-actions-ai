package com.sparta.copang.AI.domain.repository;

import com.sparta.copang.AI.domain.model.AI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AIRepository extends JpaRepository<AI, UUID> {
}
