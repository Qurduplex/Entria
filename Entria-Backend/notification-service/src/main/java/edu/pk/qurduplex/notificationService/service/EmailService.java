package edu.pk.qurduplex.notificationService.service;

import edu.pk.qurduplex.notificationService.config.EmailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailProperties emailProperties;

    public void sendVerificationEmail(String toEmail, String verificationCode) {
        Context context = new Context();
        context.setVariable("code", verificationCode);

        String htmlContent = templateEngine.process("verification-email", context);

        sendHtmlEmail(toEmail, "Account Verification Code", htmlContent);
    }

    public void sendResetPasswordEmail(String toEmail, String resetCode) {
        Context context = new Context();
        context.setVariable("code", resetCode);

        String htmlContent = templateEngine.process("reset-password-email", context);

        sendHtmlEmail(toEmail, "Reset Password Code", htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailProperties.getSenderAddress());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email successfully sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Error while sending email to {}: {}", to, e.getMessage());
        }
    }
}
