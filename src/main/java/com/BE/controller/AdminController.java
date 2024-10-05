package com.BE.controller;


import com.BE.service.implementServices.AdminServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/admin")
@SecurityRequirement(name ="api")
public class AdminController {

        @Autowired
        AdminServiceImpl adminService;

    @PostMapping(value = "/upload-csv", consumes = "multipart/form-data")
    @Operation(summary = "Upload CSV file of students")
    public ResponseEntity<String> uploadCsv(@RequestPart(value = "file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.status(400).body("File is empty");
        adminService.importCSV(file);
        return ResponseEntity.ok("CSV uploaded and students saved");
    }}
