package com.ma_sante_assurance.notification;

public interface EmailService {
    void sendOtp(String email, String code);
    void sendCardNumber(String email, String fullName, String numeroAssurance);
}
