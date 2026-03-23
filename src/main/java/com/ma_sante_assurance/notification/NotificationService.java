package com.ma_sante_assurance.notification;

public interface NotificationService {
    void sendNumeroAssurance(String fullName, String code, String email, String telephone);
    void sendCarteAssurance(String fullName, String numeroAssurance, String email, String telephone);
}
