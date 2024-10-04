package com.BE.controller;


import com.BE.service.implementServices.AdminService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/admin")
@SecurityRequirement(name ="api")
public class AdminController {

        @Autowired
    AdminService adminService;

    @PostMapping(value = "/upload-csv", consumes = "multipart/form-data")
    @Operation(summary = "Upload CSV file of students")

    public ResponseEntity<String> uploadCsv(@RequestPart(value = "file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.status(400).body("File is empty");
            adminService.importCsv(file);
        return ResponseEntity.ok("CSV uploaded and students saved");
    }}
