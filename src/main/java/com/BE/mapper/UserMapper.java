package com.BE.mapper;

import com.BE.model.entity.User;
import com.BE.model.request.AuthenticationRequest;
import com.BE.model.response.AuthenticationResponse;
import com.BE.model.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(AuthenticationRequest request);

    UserResponse toUserResponse(User user);

    AuthenticationResponse toAuthenticationResponse(User user);


    void updateUser(@MappingTarget User user, AuthenticationRequest request);


}
