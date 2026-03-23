package com.ma_sante_assurance.notification;

public interface SmsService {
    void sendOtp(String phone, String code);
    void sendCardNumber(String phone, String fullName, String numeroAssurance);
}
