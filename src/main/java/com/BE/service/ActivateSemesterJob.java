package com.BE.service;

import com.BE.enums.SemesterEnum;
import com.BE.model.entity.Semester;
import com.BE.repository.SemesterRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class ActivateSemesterJob  implements Job {
    @Autowired
    SemesterRepository semesterRepository;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String semesterIdStr = jobExecutionContext.getJobDetail().getJobDataMap().getString("semesterId");
        UUID semesterId = UUID.fromString(semesterIdStr);

        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new JobExecutionException("Semester not found for ID: " + semesterId));

        if (semester.getStatus() == SemesterEnum.UPCOMING) {
            semester.setStatus(SemesterEnum.ACTIVE);
            semesterRepository.save(semester);
            System.out.println("Semester " + semesterId + " has been activated.");
        }
    }
}
