package com.BE.mapper;

import com.BE.model.entity.Semester;
import com.BE.model.entity.User;
import com.BE.model.request.AuthenticationRequest;
import com.BE.model.request.SemesterRequest;
import com.BE.model.response.SemesterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SemesterMapper {
    Semester toSemester(SemesterRequest semesterRequest);
    SemesterResponse toSemesterResponse(Semester semester);
    void updateSemester(@MappingTarget Semester semester, SemesterRequest semesterRequest);
}
