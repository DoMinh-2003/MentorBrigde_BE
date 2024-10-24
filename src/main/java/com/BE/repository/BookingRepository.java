package com.BE.repository;

import com.BE.enums.BookingStatusEnum;
import com.BE.model.entity.Booking;
import com.BE.model.entity.Semester;
import com.BE.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID>, JpaSpecificationExecutor<Booking>{
    boolean existsByTimeFrameIdAndStatus(UUID timeFrameId, BookingStatusEnum status);


    @Query("SELECT b FROM Booking b WHERE b.mentor = :mentor AND b.status = :status AND b.semester = :semester AND MONTH(b.timeFrame.timeFrameFrom) = :month")
    List<Booking> findByMentorAndStatusAndSemesterAndTimeFrameMonth(@Param("mentor") User mentor,
                                                                    @Param("status") BookingStatusEnum status,
                                                                    @Param("semester") Semester semester,
                                                                    @Param("month") int month);


    @Query("SELECT b FROM Booking b JOIN b.team t JOIN t.userTeams ut WHERE (b.student = :student OR ut.user = :student) AND b.status = :status AND b.semester = :semester AND MONTH(b.timeFrame.timeFrameFrom) = :month")
    List<Booking> findByStudentOrTeamMemberAndStatusAndSemesterAndTimeFrameMonth(@Param("student") User student,
                                                                                 @Param("status") BookingStatusEnum status,
                                                                                 @Param("semester") Semester semester,
                                                                                 @Param("month") int month);


    Optional<Booking> findByIdAndStatus(UUID id, BookingStatusEnum status);
//    List<Booking>  findByStudentName
}
