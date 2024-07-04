package com.BE.controller;


import com.BE.model.entity.User;
import com.BE.model.request.*;
import com.BE.model.response.AuthenticationResponse;
import com.BE.service.AuthenticationService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@SecurityRequirement(name ="api")
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    ResponseHandler responseHandler;


    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody AuthenticationRequest user){
        return responseHandler.response(200, "Register success!", authenticationService.register(user));
    }

    @PostMapping("/login")
    public  ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequestDTO loginRequestDTO){
        return responseHandler.response(200, "Login success!", authenticationService.authenticate(loginRequestDTO));
    }

    @PostMapping("/login-google")
    private ResponseEntity checkLoginGoogle(@RequestBody LoginGoogleRequest loginGGRequest){
        return responseHandler.response(200, "Login Google success!", authenticationService.loginGoogle(loginGGRequest));
    }

    @PostMapping("forgot-password")
    public ResponseEntity forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authenticationService.forgotPasswordRequest(forgotPasswordRequest.getEmail());
        return ResponseEntity.ok( "Forgot Password successfully");
    }

    @PatchMapping("reset-password")
    public ResponseEntity resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        authenticationService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok( "Reset Password successfully");

    }



    @GetMapping("/testRole")
    public ResponseEntity testRole(){
        return ResponseEntity.ok("Test Role User Successfully");
    }



    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-only")
    public ResponseEntity admin(){
        return ResponseEntity.ok(authenticationService.admin());
    }




}
