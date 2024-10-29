package com.BE.repository;

import com.BE.model.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    boolean existsByBookingIdAndUserId(UUID bookingId, UUID userId);
    Optional<Feedback> findById(UUID id);
    void deleteById(UUID id);
    List<Feedback> findByBookingId(UUID bookingId);
    List<Feedback> findByBookingIdIn(List<UUID> bookingIds);
}
