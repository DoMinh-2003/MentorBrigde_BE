package com.BE.service.interfaceServices;

import com.BE.model.entity.Semester;
import com.BE.model.request.SemesterRequest;
import com.BE.model.response.SemesterResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ISemesterService {
        SemesterResponse createNewSemester(SemesterRequest semesterRequest);
        Semester getCurrentSemester();
        SemesterResponse updateSemester(UUID semesterID,SemesterRequest semesterRequest);
        Page<SemesterResponse> searchSemesters(String code, String name, int page, int size);
}

