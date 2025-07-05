package com.healthyForum.service;

import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(User user, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        if (user.getAccount() == null) {
            throw new IllegalStateException("User account is null. Cannot send verification email.");
        }
        sendVerificationEmail(user, user.getAccount(), request);
    }

    public void sendVerificationEmail(User user, UserAccount account, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "your-email@gmail.com"; // Should be the same as in application.properties
        String senderName = "Healthy Forum";
        String subject = "Please verify your registration";

        // Construct the verification URL
        String siteURL = request.getRequestURL().toString().replace(request.getServletPath(), "");
        String verifyURL = siteURL + "/verify?code=" + account.getVerificationCode();

        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Healthy Forum.";

        content = content.replace("[[name]]", user.getFullName());
        content = content.replace("[[URL]]", verifyURL);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(User user, String resetLink) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "your-email@gmail.com"; // Should match application.properties
        String senderName = "Healthy Forum";
        String subject = "Password Reset Request";

        String content = "Dear [[name]],<br>"
                + "You requested a password reset. Click the link below to reset your password:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">RESET PASSWORD</a></h3>"
                + "If you did not request this, please ignore this email.<br>"
                + "Thank you,<br>"
                + "Healthy Forum.";

        content = content.replace("[[name]]", user.getFullName());
        content = content.replace("[[URL]]", resetLink);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }
} 