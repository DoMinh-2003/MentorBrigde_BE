package com.BE.service.interfaceServices;

import com.BE.model.entity.Notification;
import com.BE.model.entity.User;

import java.util.List;

public interface INotificationService {
    void createNotification(String title, String message, User user);
    List<Notification> getNotificationsByUserId();
}
