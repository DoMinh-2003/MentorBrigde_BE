package com.BE.repository;

import com.BE.enums.SemesterEnum;
import com.BE.model.entity.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface SemesterRepository extends JpaRepository<Semester, UUID> {

    @Query("SELECT s FROM Semester s WHERE :today BETWEEN s.dateFrom AND s.dateTo")
    Semester findCurrentSemester(@Param("today") LocalDateTime today);

    Semester findSemesterByCode(String Code);

    Optional<Semester> findByStatus(SemesterEnum semesterEnum);
    Page<Semester> findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(String code, String name, Pageable pageable);



}
