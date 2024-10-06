package com.BE.controller;

import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.UserResponse;
import com.BE.service.interfaceServices.IStudentService;
import com.BE.service.interfaceServices.ITeamService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@SecurityRequirement(name = "api")
@Tag(name = "Student Controller")
public class StudentController {
    private final IStudentService studentService;
    private final ITeamService teamService;
    private final ResponseHandler<Object> responseHandler;

    public StudentController(IStudentService studentService,
                             ITeamService teamService,
                             ResponseHandler<Object> responseHandler) {
        this.studentService = studentService;
        this.teamService = teamService;
        this.responseHandler = responseHandler;
    }

    @GetMapping("/students")
    public ResponseEntity<DataResponseDTO<Object>> searchStudents(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "1", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int size,
            @Parameter(description = "Sort by", schema = @Schema(allowableValues = {"studentCode", "fullName", "dayOfBirth"}))
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction", schema = @Schema(allowableValues = {"asc", "desc"}))
            @RequestParam(required = false) String sortDirection) {

        // Check if the search term is provided
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new DataResponseDTO<>(400, "Search term is required.", null));
        }

        // Search by name or code
        Page<UserResponse> result = studentService.searchStudents(searchTerm, offset, size, sortBy, sortDirection);

        return responseHandler.response(200, "Search successful!", result);
    }


    @PostMapping("/team")
    public ResponseEntity<DataResponseDTO<Object>> createTeam() {
        return responseHandler.response(200, "Create group success!", teamService.createTeam());
    }

}

