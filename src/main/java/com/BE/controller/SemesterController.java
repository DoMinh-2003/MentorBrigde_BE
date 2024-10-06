package com.BE.controller;


import com.BE.model.request.SemesterRequest;
import com.BE.model.response.SemesterResponse;
import com.BE.service.implementServices.SemesterImpl;
import com.BE.service.interfaceServices.ISemesterService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    @PutMapping("{id}")
    public ResponseEntity<SemesterResponse> updateSemester(@PathVariable UUID id, @Valid @RequestBody SemesterRequest semesterRequest){
        return  responseHandler.response(200,"Update Semester Successfully",semesterService.updateSemester(id,semesterRequest));
    }
    @GetMapping("")
    public ResponseEntity getAllSemesters(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SemesterResponse> data = semesterService.searchSemesters(code, name, page, size);
        return  responseHandler.response(200,"Get Data SuccessFully", data);
    }
}
