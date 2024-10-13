package com.BE.repository;


import com.BE.model.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConfigRepository extends JpaRepository<Config, UUID> {
  Config findFirstBy();


}
