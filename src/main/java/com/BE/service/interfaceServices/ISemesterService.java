package com.BE.service.interfaceServices;

import com.BE.model.request.SemesterRequest;
import com.BE.model.response.SemesterResponse;

public interface ISemesterService {
        SemesterResponse createNewSemester(SemesterRequest semesterRequest);
}

