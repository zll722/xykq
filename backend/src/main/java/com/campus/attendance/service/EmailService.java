package com.campus.attendance.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    
    private final SystemConfigService systemConfigService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender javaMailSender, SystemConfigService systemConfigService) {
        this.javaMailSender = javaMailSender;
        this.systemConfigService = systemConfigService;
    }

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("【智能校园考勤系统】登录验证码");
        message.setText("您的登录验证码是：" + code + "。验证码在5分钟内有效，请勿泄露给他人。");
        javaMailSender.send(message);
    }

    public void sendResetPasswordCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("【智能校园考勤系统】重置密码验证码");
        message.setText("您正在进行密码重置操作。您的验证码是：" + code + "。验证码在5分钟内有效，请勿泄露给他人。");
        javaMailSender.send(message);
    }

    public void sendRegistrationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("【智能校园考勤系统】用户注册验证码");
        message.setText("欢迎注册智能校园考勤系统。您的验证码是：" + code + "。验证码在5分钟内有效，请勿泄露给他人。");
        javaMailSender.send(message);
    }
}
