package com.BE.service.implementServices;


import com.BE.enums.SemesterEnum;
import com.BE.exception.exceptions.DateException;
import com.BE.mapper.SemesterMapper;
import com.BE.model.entity.Semester;
import com.BE.model.request.SemesterRequest;
import com.BE.model.response.SemesterResponse;
import com.BE.repository.SemesterRepository;
import com.BE.service.interfaceServices.ISemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SemesterImpl implements ISemesterService {
    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    SemesterMapper semesterMapper;
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
        return semesterMapper.toSemesterResponse(semester);
    }

    @Override
    public Semester getCurrentSemester() {
        LocalDate today = LocalDate.now();
        return semesterRepository.findCurrentSemester(today);
    }


}
