package com.example.emailservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class Emailservices {

    private static final Logger logger = LoggerFactory.getLogger(Emailservices.class);

    private final RestTemplate restTemplate;
    private boolean testMode = false;

    @Value("${zeptomail.api.key}")
    private String apiKey;

    @Value("${zeptomail.api.url}")
    private String apiUrl;

    public Emailservices(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public boolean isValidEmail(String email) {
        return email != null && email.matches(".+@.+\\..+");
    }

    public void sendWithAttachment(String[] recipients, String subject, String body, String encodedPdf, String filename) {
        try {
            for (String email : recipients) {
                if (!isValidEmail(email)) {
                    logger.warn("⚠️ Invalid email address: {}", email);
                    if (!testMode) throw new IllegalArgumentException("Invalid email: " + email);
                    return;
                }
            }

            logger.info("📤 Sending email with attachment to: {} | Subject: {}", Arrays.toString(recipients), subject);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("Authorization", "Zoho-enczapikey " + apiKey);

            Map<String, Object> payload = new HashMap<>();
            payload.put("from", Map.of("address", "info@tamsen.in", "name", "Tamsen Support Team"));

            List<Map<String, Object>> toList = new ArrayList<>();
            for (String email : recipients) {
                Map<String, Object> emailAddress = new HashMap<>();
                emailAddress.put("address", email);

                Map<String, Object> recipient = new HashMap<>();
                recipient.put("email_address", emailAddress);

                toList.add(recipient);
            }
            payload.put("to", toList);

            payload.put("subject", subject);
            payload.put("htmlbody", body);

            Map<String, Object> attachment = new HashMap<>();
            attachment.put("name", filename);
            attachment.put("mime_type", "application/pdf");
            attachment.put("content", encodedPdf);
            payload.put("attachments", new Object[]{attachment});

            ObjectMapper mapper = new ObjectMapper();
            String rawJson = mapper.writeValueAsString(payload);
            logger.debug("📤 Raw JSON with attachment: {}", rawJson);

            HttpEntity<String> request = new HttpEntity<>(rawJson, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            logger.info("✅ ZeptoMail response: {}", response.getBody());

        } catch (HttpClientErrorException.Forbidden e) {
            String responseBody = e.getResponseBodyAsString();
            if (responseBody.contains("Trial mail sending limit exceeded") || responseBody.contains("Per day limit exhausted")) {
                logger.error("🚫 ZeptoMail trial quota exceeded. You’ve hit the 110-email/day limit. Try again tomorrow.");
            } else {
                logger.error("❌ Forbidden error: {}", responseBody);
            }
            if (!testMode) throw new RuntimeException("ZeptoMail quota exceeded or access denied.");
        } catch (HttpStatusCodeException e) {
            logger.error("❌ Email sending failed: {} : {}", e.getStatusCode(), e.getResponseBodyAsString());
            if (!testMode) throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error("❌ Unexpected error: {}", e.getMessage(), e);
            if (!testMode) throw new RuntimeException(e);
        }
    }
}







































/*
 * package com.example.emailservice;
 * 
 * import org.slf4j.Logger; import org.slf4j.LoggerFactory; import
 * org.springframework.beans.factory.annotation.Value; import
 * org.springframework.http.*; import org.springframework.stereotype.Service;
 * import org.springframework.web.client.RestTemplate;
 * 
 * import java.util.*;
 * 
 * @Service public class Emailservices {
 * 
 * private static final Logger logger =
 * LoggerFactory.getLogger(Emailservices.class);
 * 
 * private final RestTemplate restTemplate; private boolean testMode = false;
 * 
 * @Value("${zeptomail.api.key}") private String apiKey;
 * 
 * @Value("${zeptomail.api.url}") private String apiUrl;
 * 
 * public Emailservices(RestTemplate restTemplate) { this.restTemplate =
 * restTemplate; }
 * 
 * public void setTestMode(boolean testMode) { this.testMode = testMode; }
 * 
 * public boolean isValidEmail(String email) { return email != null &&
 * email.matches(".+@.+\\..+"); }
 * 
 * public void sendWithLink(String[] recipients, String subject, String body,
 * String pdfLink) { try { for (String email : recipients) { if
 * (!isValidEmail(email)) { logger.warn("⚠️ Invalid email address: {}", email);
 * if (!testMode) throw new IllegalArgumentException("Invalid email: " + email);
 * return; } }
 * 
 * logger.info("📤 Sending email to: {} | Subject: {}",
 * Arrays.toString(recipients), subject);
 * 
 * HttpHeaders headers = new HttpHeaders();
 * headers.setContentType(MediaType.APPLICATION_JSON);
 * headers.setAccept(List.of(MediaType.APPLICATION_JSON));
 * headers.set("Authorization", "Zoho-encapikey " + apiKey);
 * 
 * Map<String, Object> payload = new HashMap<>(); payload.put("bounce_address",
 * "bounce@tamsen.in"); payload.put("from", Map.of("address", "info@tamsen.in",
 * "name", "Tamsen Support Team"));
 * 
 * List<Map<String, Object>> toList = Arrays.stream(recipients) .map(email -> {
 * Map<String, Object> inner = new HashMap<>(); inner.put("address", email);
 * 
 * Map<String, Object> outer = new HashMap<>(); outer.put("email_address",
 * inner); return outer; }) .toList(); payload.put("to", toList);
 * 
 * payload.put("subject", subject); payload.put("htmlbody", body +
 * "<br><br><a href='" + pdfLink + "'>📄 Download Report</a>");
 * 
 * HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
 * ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request,
 * String.class);
 * 
 * logger.info("✅ ZeptoMail response: {}", response.getBody());
 * 
 * } catch (Exception e) { logger.error("❌ Email sending failed: {}",
 * e.getMessage(), e); if (!testMode) throw new RuntimeException(e); } } }
 */
































/*package com.example.emailservice;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class Emailservices {

    private static final Logger logger = LoggerFactory.getLogger(Emailservices.class);

    private final RestTemplate restTemplate;
    private boolean testMode = false;

    @Value("${zeptomail.api.key}")
    private String apiKey;

    @Value("${zeptomail.api.url}")
    private String apiUrl;

    public Emailservices(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public boolean isValidEmail(String email) {
        return email != null && email.matches(".+@.+\\..+");
    }

    public void sendWithLink(String[] recipients, String subject, String body, String pdfLink) {
        try {
            for (String email : recipients) {
                if (!isValidEmail(email)) {
                    logger.warn("⚠️ Invalid email address: {}", email);
                    if (!testMode) throw new IllegalArgumentException("Invalid email: " + email);
                    return;
                }
            }

            logger.info("📤 Sending email to: {} | Subject: {}", Arrays.toString(recipients), subject);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("Authorization", "Zoho-encapikey " + apiKey);

            Map<String, Object> payload = new HashMap<>();
            payload.put("bounce_address", "bounce@tamsen.in");
            payload.put("from", Map.of("address", "info@tamsen.in", "name", "Tamsen Support Team"));

            List<Map<String, Object>> toList = Arrays.stream(recipients)
                .map(email -> Map.of("email_address", Map.of("address", email)))
                .toList();
            payload.put("to", toList);

            payload.put("subject", subject);
            payload.put("htmlbody", body + "<br><br><a href='" + pdfLink + "'>📄 Download Report</a>");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            logger.info("✅ ZeptoMail response: {}", response.getBody());

        } catch (Exception e) {
            logger.error("❌ Email sending failed: {}", e.getMessage(), e);
            if (!testMode) throw new RuntimeException(e);
        }
    }
}


*/























































































// SMTP java mail sender to change zeptoapi mail rest api template changed 28/10/2025
/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

@Service
public class Emailservices {

    private static final Logger logger = LoggerFactory.getLogger(Emailservices.class);

    @Autowired private JavaMailSender mailSender;
    private boolean testMode = false;

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public boolean isValidEmail(String email) {
        return email != null && email.matches(".+@.+\\..+");
    }

    public void sendWithAttachment(String[] recipients, String subject, String body, byte[] pdfBytes) {
        sendWithAttachment(recipients, subject, body, pdfBytes, "Report.pdf");
    }

    public void sendWithAttachment(String[] recipients, String subject, String body, byte[] pdfBytes, String filename) {
        try {
            for (String email : recipients) {
                if (!isValidEmail(email)) {
                    logger.warn("⚠️ Invalid email address: {}", email);
                    if (!testMode) throw new IllegalArgumentException("Invalid email: " + email);
                    return;
                }
            }

            logger.info("📤 Sending email to: {} | Subject: {}", Arrays.toString(recipients), subject);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            try {
                helper.setFrom("info@tamsen.in", "Tamsen Support Team");
            } catch (UnsupportedEncodingException e) {
                logger.error("❌ Invalid encoding in sender name: {}", e.getMessage(), e);
                helper.setFrom("info@tamsen.in");
            }

            helper.setTo(recipients);
            helper.setSubject(subject);
            helper.setText(body, true);

            if (pdfBytes != null && filename != null) {
                helper.addAttachment(filename, new ByteArrayResource(pdfBytes));
            }

            mailSender.send(message);
            logger.info("✅ Email sent to: {}", Arrays.toString(recipients));
        } catch (IllegalArgumentException e) {
            throw e; // ✅ Let validation errors propagate
        } catch (Exception e) {
            logger.error("❌ Email sending failed: {}", e.getMessage(), e);
            if (!testMode) throw new RuntimeException(e);
        }
    }
}


*/























































/*
 * package com.example.emailservice;
 * 
 * import java.io.UnsupportedEncodingException; import java.util.Arrays;
 * 
 * import org.slf4j.Logger; import org.slf4j.LoggerFactory; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.core.io.ByteArrayResource; import
 * org.springframework.mail.javamail.JavaMailSender; import
 * org.springframework.mail.javamail.MimeMessageHelper; import
 * org.springframework.stereotype.Service;
 * 
 * import jakarta.mail.MessagingException; import
 * jakarta.mail.internet.MimeMessage;
 * 
 * @Service public class Emailservices {
 * 
 * private static final Logger logger =
 * LoggerFactory.getLogger(Emailservices.class);
 * 
 * static { logger.
 * info("Emailservices initialized: ready to send emails with PDF attachments."
 * ); }
 * 
 * @Autowired private JavaMailSender mailSender;
 * 
 * private boolean testMode = false;
 * 
 * public void setTestMode(boolean testMode) { this.testMode = testMode; }
 * 
 * public void sendWithAttachment(String[] recipients, String subject, String
 * body, byte[] pdfBytes) { sendWithAttachment(recipients, subject, body,
 * pdfBytes, "Report.pdf"); }
 * 
 * public void sendWithAttachment(String[] recipients, String subject, String
 * body, byte[] pdfBytes, String filename) { try { for (String email :
 * recipients) { if (email == null || !email.matches(".+@.+\\..+")) {
 * logger.warn("⚠️ Invalid email address: {}", email); return; } }
 * 
 * logger.info("📤 Sending email to: {} | Subject: {}",
 * Arrays.toString(recipients), subject);
 * 
 * MimeMessage message = mailSender.createMimeMessage(); MimeMessageHelper
 * helper = new MimeMessageHelper(message, true, "UTF-8");
 * 
 * try { helper.setFrom("info@tamsen.in", "Chandru C"); } catch
 * (UnsupportedEncodingException e) {
 * logger.error("❌ Invalid encoding in sender name: {}", e.getMessage(), e);
 * helper.setFrom("info@tamsen.in"); }
 * 
 * helper.setTo(recipients); helper.setSubject(subject); helper.setText(body,
 * true); // ✅ Enables HTML rendering
 * 
 * if (pdfBytes != null && filename != null) { helper.addAttachment(filename,
 * new ByteArrayResource(pdfBytes)); }
 * 
 * mailSender.send(message); logger.info("✅ Email sent to: {}",
 * Arrays.toString(recipients)); } catch
 * (org.springframework.mail.MailSendException e) {
 * logger.error("❌ MailSendException during email send: {}", e.getMessage(), e);
 * if (testMode) throw new RuntimeException(e); } catch (MessagingException e) {
 * logger.error("❌ MessagingException during email send: {}", e.getMessage(),
 * e); if (testMode) throw new RuntimeException(e); } } }
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */









































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