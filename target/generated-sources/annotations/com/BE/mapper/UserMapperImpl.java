package com.BE.mapper;

import com.BE.model.entity.User;
import com.BE.model.request.AuthenticationRequest;
import com.BE.model.response.AuthenticationResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-24T12:32:14+0700",
    comments = "version: 1.6.0.Beta2, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(AuthenticationRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setFirstName( request.getFirstName() );
        user.setLastName( request.getLastName() );
        user.setEmail( request.getEmail() );
        user.setUsername( request.getUsername() );
        user.setPassword( request.getPassword() );

        return user;
    }

    @Override
    public AuthenticationResponse toAuthenticationResponse(User user) {
        if ( user == null ) {
            return null;
        }

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();

        authenticationResponse.setProjects( mapActiveProjects( user.getProjects() ) );
        authenticationResponse.setId( user.getId() );
        authenticationResponse.setFirstName( user.getFirstName() );
        authenticationResponse.setLastName( user.getLastName() );
        authenticationResponse.setUsername( user.getUsername() );
        authenticationResponse.setRole( user.getRole() );

        return authenticationResponse;
    }

    @Override
    public void updateUser(User user, AuthenticationRequest request) {
        if ( request == null ) {
            return;
        }

        user.setFirstName( request.getFirstName() );
        user.setLastName( request.getLastName() );
        user.setEmail( request.getEmail() );
        user.setUsername( request.getUsername() );
        user.setPassword( request.getPassword() );
    }
}
