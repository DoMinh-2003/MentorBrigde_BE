package com.BE.controller;

import com.BE.service.interfaceServices.INotificationService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "api")
public class NotificationController {
    @Autowired
    private INotificationService notificationService;
    @Autowired
    private ResponseHandler responseHandler;
    @GetMapping("/api/notifications")
    public ResponseEntity getNotifications() {
        return responseHandler.response(200,"Get notifications successfully",
                notificationService.getNotifications());
    }
    @PutMapping("/api/notifications")
    public ResponseEntity updateNotification() {
        notificationService.updateNotification();
        return responseHandler.response(200,"Update notification successfully",
                null);
    }

}
