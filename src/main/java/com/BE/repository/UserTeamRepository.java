package com.BE.repository;

import com.BE.enums.TeamRoleEnum;
import com.BE.model.entity.Team;
import com.BE.model.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserTeamRepository extends JpaRepository<UserTeam, UUID> {
    boolean existsByUserId(UUID userId);
    Optional<UserTeam> findByUserId(UUID userId);
    List<UserTeam> findByUserIdAndRole(UUID userId, TeamRoleEnum role);
}
