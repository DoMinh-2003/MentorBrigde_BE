package com.BE.service.interfaceServices;


import com.BE.model.entity.User;
import com.BE.model.request.*;
import com.BE.model.response.AuthenResponse;
import com.BE.model.response.AuthenticationResponse;


public interface AuthenticationService {
    User register(AuthenticationRequest request);
    AuthenticationResponse authenticate(LoginRequestDTO request);
    AuthenticationResponse loginGoogle (LoginGoogleRequest loginGoogleRequest);
    void forgotPasswordRequest(String email);
    User resetPassword(ResetPasswordRequest resetPasswordRequest);
    String admin();
    AuthenResponse refresh(RefreshRequest refreshRequest);
    void logout(RefreshRequest refreshRequest);
}
