package edu.pk.qurduplex.notificationService.service;

import edu.pk.qurduplex.notificationService.config.EmailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

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

        Map<String, String> inlineImages = new HashMap<>();
        inlineImages.put("logoEntria", "static/images/Entria.png");
        inlineImages.put("iconSend", "static/images/send.png");

        sendHtmlEmail(toEmail, "Account Verification Code", htmlContent, inlineImages);
    }

    public void sendResetPasswordEmail(String toEmail, String resetCode) {
        Context context = new Context();
        context.setVariable("code", resetCode);

        String htmlContent = templateEngine.process("reset-password-email", context);

        Map<String, String> inlineImages = new HashMap<>();
        inlineImages.put("logoEntria", "static/images/Entria.png");
        inlineImages.put("iconLock", "static/images/lock.png");

        sendHtmlEmail(toEmail, "Reset Password Code", htmlContent, inlineImages);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent, Map<String, String> inlineImages) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailProperties.getSenderAddress());
            helper.setTo(to);
            helper.setSubject(subject);

            helper.setText(htmlContent, true);

            if (inlineImages != null && !inlineImages.isEmpty()) {
                for (Map.Entry<String, String> image : inlineImages.entrySet()) {
                    helper.addInline(image.getKey(), new ClassPathResource(image.getValue()));
                }
            }

            mailSender.send(message);
            log.info("Email successfully sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Error while sending email to {}: {}", to, e.getMessage());
        }
    }
}
