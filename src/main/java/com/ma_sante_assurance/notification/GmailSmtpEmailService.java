package com.ma_sante_assurance.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service("gmailEmail")
public class GmailSmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final String senderEmail;
    private final String senderName;

    public GmailSmtpEmailService(JavaMailSender mailSender,
                                 @Value("${spring.mail.username}") String senderEmail,
                                 @Value("${MAIL_FROM_NAME:M&A Sante Assurance}") String senderName) {
        this.mailSender = mailSender;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
    }

    @Override
    public void sendOtp(String email, String code) {
        sendEmail(
                email,
                "Votre code OTP",
                buildOtpHtml(code),
                "Votre code OTP M&A Sante Assurance : " + code + " (valable 5 min)"
        );
    }

    @Override
    public void sendCardNumber(String email, String fullName, String numeroAssurance) {
        sendEmail(
                email,
                "Votre numéro de carte d'assurance",
                buildCardHtml(fullName, numeroAssurance),
                "Bonjour %s, votre numéro de carte d'assurance est : %s".formatted(fullName, numeroAssurance)
        );
    }

    private String buildOtpHtml(String code) {
        return """
                <!doctype html>
                <html lang="fr">
                <body style="margin:0;padding:0;background:#f4f7fb;font-family:Arial,Helvetica,sans-serif;color:#1f2937;">
                  <div style="max-width:640px;margin:0 auto;padding:32px 16px;">
                    <div style="background:linear-gradient(135deg,#0f766e 0%%,#14532d 100%%);border-radius:20px 20px 0 0;padding:28px 32px;color:#ffffff;">
                      <div style="font-size:14px;letter-spacing:.12em;text-transform:uppercase;opacity:.9;">M&A Sante Assurance</div>
                      <div style="font-size:28px;font-weight:700;line-height:1.2;margin-top:10px;">Verification de votre compte</div>
                    </div>
                    <div style="background:#ffffff;border:1px solid #e5e7eb;border-top:none;border-radius:0 0 20px 20px;padding:32px;">
                      <p style="margin:0 0 16px;font-size:16px;line-height:1.7;">Bonjour,</p>
                      <p style="margin:0 0 24px;font-size:16px;line-height:1.7;">
                        Votre code OTP est demande pour securiser votre compte. Utilisez le code ci-dessous pour continuer:
                      </p>
                      <div style="display:inline-block;background:#ecfeff;border:1px solid #14b8a6;border-radius:16px;padding:18px 24px;margin:8px 0 24px;">
                        <div style="font-size:13px;color:#0f766e;text-transform:uppercase;letter-spacing:.08em;margin-bottom:8px;">Code OTP</div>
                        <div style="font-size:34px;font-weight:800;letter-spacing:.28em;color:#0f172a;">%s</div>
                      </div>
                      <p style="margin:0 0 12px;font-size:14px;line-height:1.7;color:#4b5563;">
                        Ce code est valable 5 minutes.
                      </p>
                      <p style="margin:0;font-size:14px;line-height:1.7;color:#4b5563;">
                        Si vous n'etes pas a l'origine de cette demande, vous pouvez ignorer ce message.
                      </p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(code);
    }

    private String buildCardHtml(String fullName, String numeroAssurance) {
        return """
                <!doctype html>
                <html lang="fr">
                <body style="margin:0;padding:0;background:#f4f7fb;font-family:Arial,Helvetica,sans-serif;color:#1f2937;">
                  <div style="max-width:680px;margin:0 auto;padding:32px 16px;">
                    <div style="background:linear-gradient(135deg,#0f766e 0%%,#1d4ed8 100%%);border-radius:20px 20px 0 0;padding:28px 32px;color:#ffffff;">
                      <div style="font-size:14px;letter-spacing:.12em;text-transform:uppercase;opacity:.9;">M&A Sante Assurance</div>
                      <div style="font-size:28px;font-weight:700;line-height:1.2;margin-top:10px;">Votre carte d'assurance</div>
                    </div>
                    <div style="background:#ffffff;border:1px solid #e5e7eb;border-top:none;border-radius:0 0 20px 20px;padding:32px;">
                      <p style="margin:0 0 16px;font-size:16px;line-height:1.7;">Bonjour %s,</p>
                      <p style="margin:0 0 24px;font-size:16px;line-height:1.7;">
                        Votre numero de carte d'assurance a ete genere avec succes. Conservez-le precieusement et utilisez-le pour vos futures demarches.
                      </p>
                      <div style="background:#eff6ff;border:1px solid #93c5fd;border-radius:18px;padding:20px 24px;margin:8px 0 24px;">
                        <div style="font-size:13px;color:#1d4ed8;text-transform:uppercase;letter-spacing:.08em;margin-bottom:8px;">Numero d'assurance</div>
                        <div style="font-size:28px;font-weight:800;letter-spacing:.04em;color:#0f172a;word-break:break-word;">%s</div>
                      </div>
                      <div style="display:inline-block;background:#f0fdf4;border:1px solid #86efac;border-radius:999px;padding:10px 16px;font-size:13px;color:#166534;">
                        Document numerique pret a etre utilise
                      </div>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(fullName, numeroAssurance);
    }

    private void sendEmail(String recipientEmail, String subject, String htmlContent, String textContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail, senderName);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(textContent, htmlContent);

            mailSender.send(message);
            log.info("Email envoyé à {} via Gmail SMTP", recipientEmail);
        } catch (MessagingException e) {
            log.error("Erreur préparation email à {}: {}", recipientEmail, e.getMessage());
            throw new RuntimeException("Échec envoi email", e);
        } catch (Exception e) {
            log.error("Erreur envoi email à {}: {}", recipientEmail, e.getMessage());
            throw new RuntimeException("Échec envoi email", e);
        }
    }
}
