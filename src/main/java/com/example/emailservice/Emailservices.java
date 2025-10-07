package com.example.emailservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class Emailservices {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailWithAttachment(String[] toEmails, String subject, String body, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmails);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.addAttachment("RFID_Report_MVL.pdf", new ByteArrayResource(pdfBytes));
            mailSender.send(message);
            System.out.println("✅ Email sent to: " + String.join(", ", toEmails));
        } catch (MessagingException e) {
            System.out.println("❌ Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
