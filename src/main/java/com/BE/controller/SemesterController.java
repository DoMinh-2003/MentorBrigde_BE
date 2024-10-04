package com.BE.controller;


import com.BE.model.request.SemesterRequest;
import com.BE.model.response.SemesterResponse;
import com.BE.service.implementServices.SemesterImpl;
import com.BE.service.interfaceServices.ISemesterService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/semester")
@SecurityRequirement(name ="api")
public class SemesterController {

    @Autowired
    ISemesterService semesterService;

    @Autowired
    ResponseHandler responseHandler;

    @PostMapping("")
    public ResponseEntity<SemesterResponse> createNewSemester(@Valid @RequestBody SemesterRequest semesterRequest){
        return  responseHandler.response(200,"Create New Semester Successfully",semesterService.createNewSemester(semesterRequest));
    }
}
