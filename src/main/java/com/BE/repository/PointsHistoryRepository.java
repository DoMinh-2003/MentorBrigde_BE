package com.BE.repository;

import com.BE.model.entity.PointsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PointsHistoryRepository extends JpaRepository<PointsHistory, UUID>, JpaSpecificationExecutor<PointsHistory> {
}
