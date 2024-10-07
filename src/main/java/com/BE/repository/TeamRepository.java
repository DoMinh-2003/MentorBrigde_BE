package com.BE.repository;

import com.BE.model.entity.Semester;
import com.BE.model.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
    int countBySemester(Semester semester);


}
