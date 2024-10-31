package com.BE.service.interfaceServices;

import com.BE.model.entity.Notification;
import com.BE.model.entity.User;

import java.util.List;
import java.util.UUID;

public interface INotificationService {
    void createNotification(String title, String message, User user, boolean sendMailStatus);
    List<Notification> getNotifications();
    void updateNotification();
    void updateNotificationByid(UUID id);
}
