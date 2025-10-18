package com.example.emailservice;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class Emailservices {

    private static final Logger logger = LoggerFactory.getLogger(Emailservices.class);

    static {
        logger.info("Emailservices initialized: ready to send emails with PDF attachments.");
    }

    @Autowired
    private JavaMailSender mailSender;

    private boolean testMode = false;

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public void sendWithAttachment(String[] recipients, String subject, String body, byte[] pdfBytes) {
        sendWithAttachment(recipients, subject, body, pdfBytes, "Report.pdf");
    }

    public void sendWithAttachment(String[] recipients, String subject, String body, byte[] pdfBytes, String filename) {
        try {
            for (String email : recipients) {
                if (email == null || !email.matches(".+@.+\\..+")) {
                    logger.warn("⚠️ Invalid email address: {}", email);
                    return;
                }
            }

            logger.info("📤 Sending email to: {} | Subject: {}", Arrays.toString(recipients), subject);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            try {
                helper.setFrom("info@tamsen.in", "Chandru C");
            } catch (UnsupportedEncodingException e) {
                logger.error("❌ Invalid encoding in sender name: {}", e.getMessage(), e);
                helper.setFrom("info@tamsen.in");
            }

            helper.setTo(recipients);
            helper.setSubject(subject);
            helper.setText(body, true); // ✅ Enables HTML rendering

            if (pdfBytes != null && filename != null) {
                helper.addAttachment(filename, new ByteArrayResource(pdfBytes));
            }

            mailSender.send(message);
            logger.info("✅ Email sent to: {}", Arrays.toString(recipients));
        } catch (org.springframework.mail.MailSendException e) {
            logger.error("❌ MailSendException during email send: {}", e.getMessage(), e);
            if (testMode) throw new RuntimeException(e);
        } catch (MessagingException e) {
            logger.error("❌ MessagingException during email send: {}", e.getMessage(), e);
            if (testMode) throw new RuntimeException(e);
        }
    }
}

























































/*
 * public void sendEmailWithAttachment(String[] toEmails, String subject, String
 * body, byte[] pdfBytes) { try { for (String email : toEmails) { if (email ==
 * null || !email.matches(".+@.+\\..+")) {
 * logger.warn("⚠️ Invalid email address: {}", email); return; }
 * logger.info("📤 Attempting to send email to: {}", email); }
 * 
 * MimeMessage message = mailSender.createMimeMessage(); MimeMessageHelper
 * helper = new MimeMessageHelper(message, true);
 * 
 * try { helper.setFrom("info@tamsen.in", "Chandru C"); } catch
 * (UnsupportedEncodingException e) {
 * logger.error("❌ Invalid encoding in sender name: {}", e.getMessage(), e);
 * helper.setFrom("info@tamsen.in"); }
 * 
 * helper.setTo(toEmails); helper.setSubject(subject); helper.setText(body,
 * true); helper.addAttachment("RFID_Report_MVL.pdf", new
 * ByteArrayResource(pdfBytes));
 * 
 * mailSender.send(message); logger.info("✅ Email sent successfully to: {}",
 * String.join(", ", toEmails)); } catch (MessagingException e) {
 * logger.error("❌ Error sending email: {}", e.getMessage(), e); if (testMode)
 * throw new RuntimeException(e); } } }
 */