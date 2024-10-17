package com.BE.repository;

import com.BE.enums.BookingStatusEnum;
import com.BE.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    boolean existsByTimeFrameIdAndStatusNotLike(UUID timeFrameId, BookingStatusEnum status);
}
