package com.BE.repository;

import com.BE.model.entity.TimeFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TimeFrameRepository extends JpaRepository<TimeFrame, UUID> {


    List<TimeFrame>  findByMentorIdAndSemesterIdOrderByTimeFrameFromAsc(UUID MentorId,UUID semesterId);

    Optional<TimeFrame> findById(UUID id);

}
