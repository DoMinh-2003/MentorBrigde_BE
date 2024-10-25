package com.BE.repository;

import com.BE.enums.SemesterEnum;
import com.BE.enums.StatusEnum;
import com.BE.model.entity.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SemesterRepository extends JpaRepository<Semester, UUID> {


//    @Query("SELECT s FROM Semester s WHERE :today BETWEEN s.dateFrom AND s.dateTo")
//    Semester findCurrentSemester(@Param("today") LocalDateTime today);


    Semester findSemesterByCode(String Code);

    Optional<Semester> findByStatus(SemesterEnum semesterEnum);
    List<Semester> findSemestersByStatus(SemesterEnum semesterEnum);
    @Query("SELECT s FROM Semester s WHERE " +
            "s.isDeleted = false AND " +
            "(:code IS NULL OR :code = '' OR LOWER(s.code) LIKE LOWER(CONCAT('%', :code, '%'))) " +
            "AND (:name IS NULL OR :name = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:status IS NULL OR s.status = :status)")
    Page<Semester> findByCodeContainingIgnoreCaseAndNameContainingIgnoreCaseAndStatus(
            @Param("code") String code,
            @Param("name") String name,
            @Param("status") SemesterEnum status,
            Pageable pageable);


    Semester findFirstByOrderByCreatedAtDesc();

    Semester findSemesterByStatus(SemesterEnum statusEnum);

}
