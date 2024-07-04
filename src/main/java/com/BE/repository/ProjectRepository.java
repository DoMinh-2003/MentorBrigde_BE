package com.BE.repository;

import com.BE.enums.StatusEnum;
import com.BE.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Optional<Project> findProjectById(UUID id);

    List<Project> findAllByStatusEnum(StatusEnum statusEnum);

}
