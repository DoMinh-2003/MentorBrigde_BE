package com.BE.mapper;


import com.BE.model.entity.Project;
import com.BE.model.request.ProjectRequest;
import com.BE.model.response.ProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    Project toProject(ProjectRequest projectRequest);
    ProjectResponse toProjectResponse(Project project);

    List<ProjectResponse> toListProjectResponse(List<Project> projects);

    void updateProject(@MappingTarget Project project , ProjectRequest request);

}
