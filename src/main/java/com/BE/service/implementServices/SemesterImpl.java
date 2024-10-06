package com.BE.service.implementServices;


import com.BE.enums.SemesterEnum;
import com.BE.exception.exceptions.DateException;
import com.BE.mapper.SemesterMapper;
import com.BE.model.entity.Semester;
import com.BE.model.request.SemesterRequest;
import com.BE.model.response.SemesterResponse;
import com.BE.repository.SemesterRepository;
import com.BE.service.ActivateSemesterJob;
import com.BE.service.DeactivateSemesterJob;
import com.BE.service.interfaceServices.ISemesterService;
import com.BE.utils.DateNowUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class SemesterImpl implements ISemesterService {
    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    SemesterMapper semesterMapper;

    @Autowired
    DateNowUtils dateNowUtils;


    @Autowired
    private  Scheduler scheduler;  // Quartz scheduler
    @Override
    public SemesterResponse createNewSemester(SemesterRequest semesterRequest) {
        LocalDateTime now = dateNowUtils.getCurrentDateTimeHCM();
        if (semesterRequest.getDateFrom() == null || semesterRequest.getDateTo() == null) {
            throw new DateException("Both Date From and Date To must be provided");
        }
        if (semesterRequest.getDateTo().isBefore(semesterRequest.getDateFrom())) {
            throw new DateException("Date To must be later than Date From");
        }
        if (semesterRequest.getDateFrom().isBefore(now)) {
            throw new DateException("Date From cannot be in the past");
        }

        if (semesterRequest.getDateTo().isBefore(now)) {
            throw new DateException("Date To cannot be in the past");
        }
            Semester semester = semesterMapper.toSemester(semesterRequest);
            semester.setStatus(SemesterEnum.UPCOMING);
            semester = semesterRepository.save(semester);

            // Lên lịch job kích hoạt kỳ học
            scheduleActivationJob(semester);
            return semesterMapper.toSemesterResponse(semester);
    }

    private void scheduleActivationJob(Semester semester) {
        try {
            // Tạo JobDetail cho ActivateSemesterJob
            JobDetail activateJobDetail = JobBuilder.newJob(ActivateSemesterJob.class)
                    .withIdentity("activateSemesterJob_" + semester.getId(), "semesters")
                    .usingJobData("semesterId", semester.getId().toString())
                    .build();

            // Tạo Trigger cho ActivateSemesterJob vào ngày `dateFrom`
            Trigger activateTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("activateTrigger_" + semester.getId(), "semesters")
                    .startAt(Date.from(semester.getDateFrom().atZone(ZoneId.systemDefault()).toInstant())) // Thời gian bắt đầu kỳ học
                    .build();

            // Lên lịch job kích hoạt kỳ học
            scheduler.scheduleJob(activateJobDetail, activateTrigger);

            // Tạo JobDetail cho DeactivateSemesterJob
            JobDetail deactivateJobDetail = JobBuilder.newJob(DeactivateSemesterJob.class)
                    .withIdentity("deactivateSemesterJob_" + semester.getId(), "semesters")
                    .usingJobData("semesterId", semester.getId().toString())
                    .build();

            // Tạo Trigger cho DeactivateSemesterJob vào ngày `dateTo`
            Trigger deactivateTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("deactivateTrigger_" + semester.getId(), "semesters")
                    .startAt(Date.from(semester.getDateTo().atZone(ZoneId.systemDefault()).toInstant())) // Thời gian kết thúc kỳ học
                    .build();

            // Lên lịch job hủy kích hoạt kỳ học
            scheduler.scheduleJob(deactivateJobDetail, deactivateTrigger);

        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to schedule jobs for semester activation and deactivation", e);
        }
    }


    @Override
    public Semester getCurrentSemester() {
        LocalDate today = LocalDate.now();
        return semesterRepository.findCurrentSemester(today);
    }


}
