package com.BE.repository;

import com.BE.enums.StatusEnum;
import com.BE.model.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface FileRepository extends JpaRepository<File,UUID> {

}
