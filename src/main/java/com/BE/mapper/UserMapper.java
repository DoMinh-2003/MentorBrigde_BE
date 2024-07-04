package com.BE.mapper;

import com.BE.enums.StatusEnum;
import com.BE.model.entity.Project;
import com.BE.model.entity.User;
import com.BE.model.request.AuthenticationRequest;
import com.BE.model.response.AuthenticationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(AuthenticationRequest request);

    @Mapping(target = "projects", qualifiedBy = MapActiveProjects.class)
    AuthenticationResponse toAuthenticationResponse(User user);

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    @interface MapActiveProjects {}
    @MapActiveProjects
    default Set<Project> mapActiveProjects(Set<Project> projects) {
        return projects.stream()
                .filter(project -> project.getStatusEnum().equals (StatusEnum.ACTIVE))
                .collect(Collectors.toSet());
    }
    void updateUser(@MappingTarget User user , AuthenticationRequest request);


}
