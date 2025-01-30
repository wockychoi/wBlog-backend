package com.mapago.service.email;

import com.mapago.mapper.email.EmailMapper;
import com.mapago.model.email.Email;
import com.mapago.service.otp.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailMapper emailMapper;

    public Email sendEmail(Email email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("mawday2025@gmail.com");
        message.setTo(email.getAddress());
        message.setSubject(email.getSubject());
        message.setText(email.getContent());
        mailSender.send(message);
        emailMapper.insertEmail(email);
        return email;
    }
}