package com.BE.service;


import com.BE.enums.StatusEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.ProjectMapper;
import com.BE.model.entity.Project;
import com.BE.model.entity.User;
import com.BE.model.request.ProjectRequest;
import com.BE.model.response.ProjectResponse;
import com.BE.repository.ProjectRepository;
import com.BE.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    AccountUtils  accountUtils;

    public List<ProjectResponse> getProjects(){
      List<Project> projects = projectRepository.findAllByStatusEnum(StatusEnum.ACTIVE);
      List<ProjectResponse> projectResponses = projectMapper.toListProjectResponse(projects);
      return projectResponses;
    }


    public ProjectResponse createProject(ProjectRequest projectRequest) {
        User user = accountUtils.getCurrentUser();
        Project project = projectMapper.toProject(projectRequest);
        project.getUsers().add(user);
        project.setStatusEnum(StatusEnum.ACTIVE);
        project = projectRepository.save(project);
        ProjectResponse projectResponse = projectMapper.toProjectResponse(project);
        return projectResponse;
    }

    public boolean userHasAccessToProject(Project project, User user) {
            return project.getUsers().stream().anyMatch(userProject -> userProject.getId().equals(user.getId()));
    }

    public ProjectResponse deleteProject(UUID id) {
        User user = accountUtils.getCurrentUser();
        Project project = projectRepository.findProjectById(id).orElseThrow(() -> new NotFoundException("Project Not Found"));
        // check xem user có trong project mới được update project
        if(!userHasAccessToProject(project, user)){
            throw new AccessDeniedException("AccessDeniedException");
        }
        project.setStatusEnum(StatusEnum.DEACTIVE);
        project = projectRepository.save(project);
        ProjectResponse projectResponse = projectMapper.toProjectResponse(project);
        return projectResponse;
    }
    public ProjectResponse updateProject(UUID id , ProjectRequest projectRequest) {
        User user = accountUtils.getCurrentUser();
        Project project = projectRepository.findProjectById(id).orElseThrow(() -> new NotFoundException("Project Not Found"));
        // check xem user có trong project mới được update project
        if(!userHasAccessToProject(project, user)){
            throw new AccessDeniedException("AccessDeniedException");
        }
        projectMapper.updateProject(project, projectRequest);
        project = projectRepository.save(project);
        ProjectResponse projectResponse = projectMapper.toProjectResponse(project);
        return projectResponse;
    }




}
