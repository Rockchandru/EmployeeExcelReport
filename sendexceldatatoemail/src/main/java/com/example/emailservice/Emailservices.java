package com.example.emailservice;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ByteArrayResource;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class Emailservices {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailWithAttachment(String[] toEmails, String subject, String body, byte[] excelBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmails);
            helper.setSubject(subject);
            helper.setText(body);

           
            helper.addAttachment("FloorSwipeSummary.xlsx", new ByteArrayResource(excelBytes));

            mailSender.send(message);
            System.out.println("✅ Email sent to: " + String.join(", ", toEmails));
        } catch (MessagingException e) {
            System.out.println("❌ Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


















































/*
 * import java.io.File;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.core.io.FileSystemResource; import
 * org.springframework.mail.javamail.JavaMailSender; import
 * org.springframework.mail.javamail.MimeMessageHelper; import
 * org.springframework.stereotype.Service; import
 * jakarta.mail.MessagingException; import jakarta.mail.internet.MimeMessage;
 * 
 * @Service public class Emailservices {
 * 
 * @Autowired private JavaMailSender mailSender;
 * 
 * public void sendEmailWithAttachment( String[] toEmails, String subject,
 * String body, File report ) { try { MimeMessage message =
 * mailSender.createMimeMessage(); MimeMessageHelper helper = new
 * MimeMessageHelper(message, true);
 * 
 * helper.setTo(toEmails); helper.setSubject(subject); helper.setText(body);
 * 
 * FileSystemResource file = new FileSystemResource(report);
 * helper.addAttachment(file.getFilename(), file);
 * 
 * mailSender.send(message); System.out.println("Email sent successfully."); }
 * catch (MessagingException e) { System.out.println("Error sending email: " +
 * e.getMessage()); e.printStackTrace(); } } }
 */
