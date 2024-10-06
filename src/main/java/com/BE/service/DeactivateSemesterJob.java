package com.BE.service;

import com.BE.enums.SemesterEnum;
import com.BE.model.entity.Semester;
import com.BE.repository.SemesterRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class DeactivateSemesterJob implements Job {

    @Autowired
    private SemesterRepository semesterRepository;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String semesterId = jobExecutionContext.getJobDetail().getJobDataMap().getString("semesterId");

        // Tìm kỳ học dựa trên ID
        Semester semester = semesterRepository.findById(UUID.fromString(semesterId))
                .orElseThrow(() -> new JobExecutionException("Semester not found for ID: " + semesterId));

        // Cập nhật trạng thái kỳ học nếu trạng thái hiện tại là ACTIVE
        if (semester.getStatus() == SemesterEnum.ACTIVE) {
            semester.setStatus(SemesterEnum.INACTIVE);
            semesterRepository.save(semester);
            System.out.println("Semester " + semesterId + " has been deactivated.");
        }
    }
}
