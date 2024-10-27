package com.BE.service.implementServices;

import com.BE.model.entity.Notification;
import com.BE.model.entity.User;
import com.BE.repository.NotificationRepository;
import com.BE.service.interfaceServices.INotificationService;
import com.BE.utils.AccountUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements INotificationService {
    private final NotificationRepository notificationRepository;
    private final AccountUtils accountUtils;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   AccountUtils accountUtils) {
        this.notificationRepository = notificationRepository;
        this.accountUtils = accountUtils;
    }

    @Override
    public void createNotification(String title, String message, User user) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        // set receiver
        notification.setUser(user);
        notificationRepository.save(notification);
    }
    @Override
    public List<Notification> getNotificationsByUserId(){
        List<Notification> notifications = notificationRepository.findByUserId(accountUtils.getCurrentUser().getId());
        notifications.forEach(notification -> notification.setIsRead(true));
        return notificationRepository.saveAll(notifications);
    }
}
