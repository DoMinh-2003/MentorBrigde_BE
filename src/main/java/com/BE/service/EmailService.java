package com.BE.service;



import com.BE.model.EmailDetail;
import com.BE.model.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@EnableAutoConfiguration
public class EmailService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMailTemplate(EmailDetail emailDetail){
        try{
            Context context = new Context();

            context.setVariable("name", emailDetail.getFullName());
            context.setVariable("button", emailDetail.getButtonValue());
            context.setVariable("link", emailDetail.getLink());

            String text = templateEngine.process("emailtemplate", context);

            // Creating a simple mail message
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            // Setting up necessary details
            mimeMessageHelper.setFrom("admin@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getRecipient());
            mimeMessageHelper.setText(text, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(mimeMessage);
        }catch (MessagingException messagingException){
            messagingException.printStackTrace();
        }
    }

    public void sendMail(User user, String subject, String description){

        try{
            // Creating a simple mail message
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            // Setting up necessary details
            mimeMessageHelper.setFrom("admin@gmail.com");
            mimeMessageHelper.setTo(user.getEmail());
            mimeMessageHelper.setText(description);
            mimeMessageHelper.setSubject(subject);
            javaMailSender.send(mimeMessage);
        }catch (MessagingException messagingException){
            messagingException.printStackTrace();
        }
    }

//
//
//        public void sendTemplateEmail(String to, String subject, String templateContent, String name, String buttonValue, String link) throws MessagingException {
//            // Tạo context cho Thymeleaf
//            Context context = new Context();
//            context.setVariable("name", name);
//            context.setVariable("button", buttonValue);
//            context.setVariable("link", link);
//
//            // Xử lý template với các biến động
//            String processedTemplate = templateEngine.process(templateContent, context);
//
//            // Tạo email với nội dung template đã xử lý
//            MimeMessage mimeMessage = mailSender.createMimeMessage();
//            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
//
//            mimeMessageHelper.setFrom("admin@gmail.com");
//            mimeMessageHelper.setTo(to);
//            mimeMessageHelper.setText(processedTemplate, true); // true để gửi email dạng HTML
//            mimeMessageHelper.setSubject(subject);
//
//            // Gửi email
//            mailSender.send(mimeMessage);
//        }



}
