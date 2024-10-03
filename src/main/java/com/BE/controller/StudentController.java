package com.BE.controller;

import com.BE.model.response.DataResponseDTO;
import com.BE.service.interfaceServices.TeamService;
import com.BE.service.interfaceServices.StudentService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@SecurityRequirement(name = "api")
@Tag(name = "Student Controller")
public class StudentController {
    private final StudentService studentService;
    private final TeamService teamService;
    private final ResponseHandler<Object> responseHandler;

    public StudentController(StudentService studentService,
                             TeamService teamService,
                             ResponseHandler<Object> responseHandler) {
        this.studentService = studentService;
        this.teamService = teamService;
        this.responseHandler = responseHandler;
    }

    @GetMapping("/students/name")
    public ResponseEntity<DataResponseDTO<Object>> getStudentsByName(@RequestParam String name,
                                               @RequestParam(defaultValue = "1", required = false) int offset,
                                               @RequestParam(defaultValue = "10", required = false) int size,
                                               @RequestParam(required = false) String sortBy) {
        return responseHandler.response(200, "Search by name success!",
                studentService.getStudentsByFullNameContaining(name, offset, size, sortBy));
    }

    @GetMapping("/students/code")
    public ResponseEntity<DataResponseDTO<Object>> getStudentsByCode(@RequestParam String code,
                                                                @RequestParam(defaultValue = "1", required = false) int offset,
                                                                @RequestParam(defaultValue = "10", required = false) int size,
                                                                @RequestParam(required = false) String sortBy) {
        return responseHandler.response(200, "Search by code success!",
                                            studentService.getStudentsByStudentCode(code, offset, size, sortBy));
    }
    @PostMapping("/team")
    public ResponseEntity<DataResponseDTO<Object>> createTeam() {
        return responseHandler.response(200, "Create group success!", teamService.createTeam());
    }

}

