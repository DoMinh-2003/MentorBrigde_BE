package com.BE.service.implementServices;


import com.BE.enums.SemesterEnum;
import com.BE.exception.exceptions.DateException;
import com.BE.mapper.SemesterMapper;
import com.BE.model.entity.Semester;
import com.BE.model.request.SemesterRequest;
import com.BE.model.response.SemesterResponse;
import com.BE.repository.SemesterRepository;
import com.BE.service.ActivateSemesterJob;
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
        if (semesterRequest.getDateFrom() == null || semesterRequest.getDateTo() == null) {
            throw new DateException("Both Date From and Date To must be provided");
        }
        if (semesterRequest.getDateTo().isBefore(semesterRequest.getDateFrom())) {
            throw new DateException("Date To must be later than Date From");
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
            JobDetail jobDetail = JobBuilder.newJob(ActivateSemesterJob.class)
                    .withIdentity("activateSemesterJob_" + semester.getId(), "semesters")
                    .usingJobData("semesterId", semester.getId().toString())
                    .build();

            // Lên lịch job vào ngày `dateFrom`
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger_" + semester.getId(), "semesters")
                    .startAt(Date.from(semester.getDateFrom().atZone(ZoneId.systemDefault()).toInstant())) // Chuyển từ LocalDateTime sang Date
                    .build();

            // Lên lịch job với Scheduler
            scheduler.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to schedule job for semester activation", e);
        }
    }

    @Override
    public Semester getCurrentSemester() {
        LocalDate today = LocalDate.now();
        return semesterRepository.findCurrentSemester(today);
    }


}
