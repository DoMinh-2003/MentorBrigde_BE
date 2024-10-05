package com.BE.repository;

import com.BE.enums.SemesterEnum;
import com.BE.model.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface SemesterRepository extends JpaRepository<Semester, UUID> {

    @Query("SELECT s FROM Semester s WHERE :today BETWEEN s.dateFrom AND s.dateTo")
    Semester findCurrentSemester(@Param("today") LocalDate today);

    Semester findSemesterByCode(String Code);

    Optional<Semester> findByStatus(SemesterEnum semesterEnum);




}
