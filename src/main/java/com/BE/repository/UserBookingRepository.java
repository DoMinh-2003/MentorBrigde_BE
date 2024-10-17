package com.BE.repository;

import com.BE.model.entity.UserBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserBookingRepository extends JpaRepository<UserBooking, UUID> {
}
