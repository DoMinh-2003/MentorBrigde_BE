package com.BE.controller;

import com.BE.exception.exceptions.BadRequestException;
import com.BE.model.request.CreateGoogleMeetRequest;
import com.BE.service.GoogleMeetService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@SecurityRequirement(name = "api")
public class GoogleMeetController {
    @Autowired
    private GoogleMeetService googleCalendarService;

    @GetMapping("/authorize")
    public RedirectView authorize() throws Exception {
        String authorizationUrl = googleCalendarService.getAuthorizationUrl();
        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/oauth2callback")
    public void handleOAuth2Callback(@RequestParam("code") String code) {
        try {
            googleCalendarService.exchangeCodeForToken(code);
        } catch (Exception e) {
            throw new BadRequestException("Không thể đăng nhập");
        }

    }

}
