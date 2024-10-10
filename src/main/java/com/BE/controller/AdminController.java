package com.BE.controller;


import com.BE.enums.RoleEnum;
import com.BE.enums.SemesterEnum;
import com.BE.model.response.UserResponse;
import com.BE.service.implementServices.AdminServiceImpl;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.UUID;

@RestController
@RequestMapping("api/admin")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class AdminController {

    @Autowired
    AdminServiceImpl adminService;

    @Autowired
    ResponseHandler responseHandler;


    @PostMapping(value = "/upload-csv", consumes = "multipart/form-data")
    @Operation(summary = "Upload CSV file of students")
    public ResponseEntity<String> uploadCsv(@RequestPart(value = "file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.status(400).body("File is empty");
        adminService.importCSV(file);
        return ResponseEntity.ok("CSV uploaded and students saved");
    }


    @GetMapping("")
    public ResponseEntity getAllUser(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) RoleEnum role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page users = adminService.searchUsers(search, role, page, size);
        return responseHandler.response(200, "Get Student Successfully", users);
    }
}
