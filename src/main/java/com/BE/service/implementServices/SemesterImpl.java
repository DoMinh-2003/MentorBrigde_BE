package com.BE.service.implementServices;


import com.BE.enums.SemesterEnum;
import com.BE.exception.exceptions.DateException;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.exception.exceptions.SemesterException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
public class SemesterImpl implements ISemesterService {
    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    DateNowUtils dateNowUtils;

    @Autowired
    SemesterMapper semesterMapper;


    @Autowired
    private Scheduler scheduler;  // Quartz scheduler

    @Override
    public SemesterResponse createNewSemester(SemesterRequest semesterRequest) {
          Semester currentSemester =  semesterRepository.findSemesterByStatus(SemesterEnum.UPCOMING);
          if(currentSemester !=null){
              throw new SemesterException("Only 1 semester is Upcoming ");
          }

        dateNowUtils.validateSemesterDates(semesterRequest);
        Semester semester = semesterMapper.toSemester(semesterRequest);
        semester.setStatus(SemesterEnum.UPCOMING);
        semester.setCreatedAt(dateNowUtils.dateNow());
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


//    @Override
//    public Semester getCurrentSemester() {
//        LocalDateTime today = dateNowUtils.getCurrentDateTimeHCM();
//        return semesterRepository.findCurrentSemester(today);
//    }

    @Override
    public SemesterResponse updateSemester(UUID semesterID,SemesterRequest semesterRequest) {
        dateNowUtils.validateSemesterDates(semesterRequest);
        Semester semester = semesterRepository.findById(semesterID).
                orElseThrow(() -> new NotFoundException("Semester Not Exist"));

        semesterMapper.updateSemester(semester, semesterRequest);
        semester = semesterRepository.save(semester);
        return semesterMapper.toSemesterResponse(semester);
    }


    public Page<SemesterResponse> searchSemesters(String code, String name, SemesterEnum status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Semester> semesterPage;

        if ((code == null || code.isEmpty()) && (name == null || name.isEmpty()) && (status == null)) {
            semesterPage = semesterRepository.findAll(pageable);
        } else {
            semesterPage = semesterRepository.findByCodeContainingIgnoreCaseAndNameContainingIgnoreCaseAndStatus(
                    code, name, status, pageable);
        }

        return semesterPage.map(semesterMapper::toSemesterResponse);
    }

    @Override
    public SemesterResponse deleteSemester(UUID id) {
        Semester semester = semesterRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Semester Not Found"));
        semester.setDeleted(true);
        semesterRepository.save(semester);
        return semesterMapper.toSemesterResponse(semester);
    }

    @Override
    public Semester getCurrentSemester() {
        return semesterRepository.findByStatus(SemesterEnum.UPCOMING).orElseThrow(() -> new NotFoundException("Semester Not Found"));
    }

}
