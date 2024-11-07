package com.BE.repository;

import com.BE.model.entity.PointsHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PointsHistoryRepository extends JpaRepository<PointsHistory, UUID> {
}
