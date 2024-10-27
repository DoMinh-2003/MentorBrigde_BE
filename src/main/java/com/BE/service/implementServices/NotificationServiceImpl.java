package com.BE.service.implementServices;

import com.BE.model.entity.Notification;
import com.BE.model.entity.User;
import com.BE.repository.NotificationRepository;
import com.BE.service.interfaceServices.INotificationService;
import com.BE.utils.AccountUtils;
import com.BE.utils.SendMailUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationServiceImpl implements INotificationService {
    private final NotificationRepository notificationRepository;
    private final AccountUtils accountUtils;
    private final SendMailUtils sendMailUtils;
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   AccountUtils accountUtils,
                                   SendMailUtils sendMailUtils) {
        this.notificationRepository = notificationRepository;
        this.accountUtils = accountUtils;
        this.sendMailUtils = sendMailUtils;
    }

    @Override
    public void createNotification(String title, String message, User user, boolean sendMailStatus) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        // set receiver
        notification.setUser(user);
        // send mail
        if (sendMailStatus) {
            sendMail(title, message, user);
        }
        notificationRepository.save(notification);
    }

    private void sendMail(String subject, String description, User user) {
        sendMailUtils.threadSendMail(user, subject, description);
    }

    @Override
    public List<Notification> getNotifications(){
        List<Notification> notifications = notificationRepository.findByUserId(accountUtils.getCurrentUser().getId());
        notifications.forEach(notification -> notification.setIsRead(true));
        return  notificationRepository.saveAll(notifications);
    }

}
