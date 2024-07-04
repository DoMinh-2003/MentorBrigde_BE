package com.BE.controller;


import com.BE.model.request.ProjectRequest;
import com.BE.model.response.ProjectResponse;
import com.BE.service.ProjectService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("api/project")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @Autowired
    ResponseHandler responseHandler;


    @GetMapping
    public ResponseEntity<ProjectResponse> get(){
        return responseHandler.response(200, "Get Project Success!", projectService.getProjects());
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest projectRequest){
        return responseHandler.response(200, "Create Project Success!", projectService.createProject(projectRequest));
    }


    @PutMapping("{id}")
    public ResponseEntity<ProjectResponse> update(@PathVariable UUID id, @Valid @RequestBody ProjectRequest projectRequest) {
        return responseHandler.response(200, "Update Project Success!", projectService.updateProject(id,projectRequest));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ProjectResponse> delete(@PathVariable UUID id) {
        return responseHandler.response(200, "Delete Project Success!", projectService.deleteProject(id));
    }




    }
