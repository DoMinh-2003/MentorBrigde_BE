package com.BE.utils;

import com.BE.model.EmailDetail;
import com.BE.model.entity.User;
import com.BE.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendMailUtils {
    @Autowired
    EmailService emailService;

    public void threadSendMail(User user, String subject, String description) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                emailService.sendMail(user, subject, description);
            }

        };
        new Thread(r).start();
    }

    public void threadSendMailTemplate(EmailDetail emailDetail) {
        System.out.println("send r nha");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                emailService.sendMailTemplate(emailDetail);
            }

        };
        new Thread(r).start();
    }
}
