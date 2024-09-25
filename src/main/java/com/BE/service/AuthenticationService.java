package com.BE.service;


import com.BE.enums.RoleEnum;
import com.BE.exception.exceptions.BadRequestException;
import com.BE.exception.exceptions.InvalidRefreshTokenException;
import com.BE.mapper.UserMapper;
import com.BE.model.EmailDetail;
import com.BE.model.request.*;
import com.BE.model.response.AuthenResponse;
import com.BE.model.response.AuthenticationResponse;
import com.BE.model.entity.User;
import com.BE.repository.UserRepository;
import com.BE.utils.AccountUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springfox.documentation.annotations.Cacheable;

import java.util.UUID;


@Service
public class AuthenticationService  {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JWTService jwtService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    EmailService emailService;

    @Autowired
    RefreshTokenService refreshTokenService;

    public User register(AuthenticationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(RoleEnum.USER);
       try {
           return userRepository.save(user);
       }catch (DataIntegrityViolationException e){
           System.out.println(e.getMessage());
           throw new DataIntegrityViolationException("Duplicate UserName");
       }
    }
//    @Cacheable()
    public AuthenticationResponse authenticate(LoginRequestDTO request){
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername().trim(),
                            request.getPassword().trim()
                    )
            );
        } catch (Exception e) {
            throw new NullPointerException("Wrong Id Or Password") ;
        }

        User user = (User) authentication.getPrincipal();
        AuthenticationResponse authenticationResponse = userMapper.toAuthenticationResponse(user);
        authenticationResponse.setToken(jwtService.generateToken(user, UUID.randomUUID().toString(),false));

        return authenticationResponse;
    }


    public AuthenticationResponse loginGoogle (LoginGoogleRequest loginGoogleRequest) {
        try{
            FirebaseToken decodeToken = FirebaseAuth.getInstance().verifyIdToken(loginGoogleRequest.getToken());
            String email = decodeToken.getEmail();
            User user = userRepository.findByEmail(email).orElseThrow();
            if(user == null) {
                user = new User();
                user.setFullName(decodeToken.getName());
                user.setEmail(email);
                user.setUsername(email);
                user.setRole(RoleEnum.USER);
                user = userRepository.save(user);
            }
            AuthenticationResponse authenticationResponse = userMapper.toAuthenticationResponse(user);
            authenticationResponse.setToken(jwtService.generateToken(user,UUID.randomUUID().toString(),false));
            return authenticationResponse;
        } catch (FirebaseAuthException e)
        {
            e.printStackTrace();
        }
        return null;
    }


    public void forgotPasswordRequest(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("Email Not Found"));

        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(user.getEmail());
        emailDetail.setSubject("Reset password for account " + user.getEmail() + "!");
        emailDetail.setMsgBody("aaa");
        emailDetail.setButtonValue("Reset Password");
        emailDetail.setFullName(user.getFullName());
        emailDetail.setLink("http://localhost:5173?token=" + jwtService.generateToken(user));

        Runnable r = new Runnable() {
            @Override
            public void run() {
                emailService.sendMailTemplate(emailDetail);
            }

        };
        new Thread(r).start();

    }

    public User resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = accountUtils.getCurrentUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        return userRepository.save(user);
    }


    public String admin(){
        String name = accountUtils.getCurrentUser().getUsername();
        return name;
    }

    public AuthenResponse refresh(RefreshRequest refreshRequest) {
        AuthenResponse authenResponse = new AuthenResponse();
        String refresh = jwtService.getRefreshClaim(refreshRequest.getToken());
        if (refreshTokenService.validateRefreshToken(refresh)) {
            System.out.println(refreshTokenService.getIdFromRefreshToken(refresh));
            User user = userRepository.findById(refreshTokenService.getIdFromRefreshToken(refresh)).orElseThrow(() -> new BadRequestException("User Not Found"));
            authenResponse.setToken(jwtService.generateToken(user,refresh,true));
        }else{
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }
        return authenResponse;
    }

    public void logout(RefreshRequest refreshRequest) {
        String refresh = jwtService.getRefreshClaim(refreshRequest.getToken());
        refreshTokenService.deleteRefreshToken(refresh);
    }
}

