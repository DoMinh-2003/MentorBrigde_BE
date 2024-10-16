package com.BE.repository;

import com.BE.model.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserTeamRepository extends JpaRepository<UserTeam, UUID> {
    boolean existsByUserId(UUID userId);
    Optional<UserTeam> findByUserId(UUID userId);
}
