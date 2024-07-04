package com.BE.mapper;

import com.BE.model.entity.Project;
import com.BE.model.entity.User;
import com.BE.model.request.ProjectRequest;
import com.BE.model.response.ProjectResponse;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-24T12:32:14+0700",
    comments = "version: 1.6.0.Beta2, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class ProjectMapperImpl implements ProjectMapper {

    @Override
    public Project toProject(ProjectRequest projectRequest) {
        if ( projectRequest == null ) {
            return null;
        }

        Project project = new Project();

        project.setName( projectRequest.getName() );

        return project;
    }

    @Override
    public ProjectResponse toProjectResponse(Project project) {
        if ( project == null ) {
            return null;
        }

        ProjectResponse projectResponse = new ProjectResponse();

        projectResponse.setId( project.getId() );
        projectResponse.setName( project.getName() );
        projectResponse.setStatusEnum( project.getStatusEnum() );
        Set<User> set = project.getUsers();
        if ( set != null ) {
            projectResponse.setUsers( new LinkedHashSet<User>( set ) );
        }

        return projectResponse;
    }

    @Override
    public List<ProjectResponse> toListProjectResponse(List<Project> projects) {
        if ( projects == null ) {
            return null;
        }

        List<ProjectResponse> list = new ArrayList<ProjectResponse>( projects.size() );
        for ( Project project : projects ) {
            list.add( toProjectResponse( project ) );
        }

        return list;
    }

    @Override
    public void updateProject(Project project, ProjectRequest request) {
        if ( request == null ) {
            return;
        }

        project.setName( request.getName() );
    }
}
