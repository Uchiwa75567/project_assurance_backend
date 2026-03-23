package com.ma_sante_assurance.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceExt {

    private final SmsService smsService;
    private final EmailService emailService;

    public void sendOtpSms(String phone, String code) {
        smsService.sendOtp(phone, code);
    }

    public void sendOtpEmail(String email, String code) {
        emailService.sendOtp(email, code);
    }
}

