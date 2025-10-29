package com.example.emailservicetest;

import com.example.emailservice.Emailservices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailservicesTest {

    @InjectMocks
    private Emailservices emailservices;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        emailservices.setTestMode(true);

        ReflectionTestUtils.setField(emailservices, "apiUrl", "https://api.zeptomail.com/v1.1/email");
        ReflectionTestUtils.setField(emailservices, "apiKey", "dummy-api-key");
    }

    @Test
    void testIsValidEmail_valid() {
        assertTrue(emailservices.isValidEmail("user@example.com"));
    }

    @Test
    void testIsValidEmail_invalid() {
        assertFalse(emailservices.isValidEmail("invalid-email"));
    }

    @Test
    void testSendWithAttachment_valid_shouldSend() {
        String[] to = {"user@example.com"};
        String[] cc = {"cc@example.com"};
        byte[] pdfBytes = "dummy-pdf-content".getBytes();
        String subject = "Test Subject";
        String body = "Test Body";
        String filename = "report.pdf";

        ResponseEntity<String> mockResponse = new ResponseEntity<>("Mail Sent Successfully", HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(mockResponse);

        assertDoesNotThrow(() ->
            emailservices.sendWithAttachment(to, cc, subject, body, pdfBytes, filename)
        );

        verify(restTemplate).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    void testSendWithAttachment_invalidToEmail_shouldSkipInTestMode() {
        String[] to = {"invalid-email"};
        String[] cc = {"cc@example.com"};
        byte[] pdfBytes = "dummy-pdf-content".getBytes();

        emailservices.sendWithAttachment(to, cc, "Subject", "Body", pdfBytes, "report.pdf");

        verify(restTemplate, never()).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    void testSendWithAttachment_invalidCcEmail_shouldSkipInTestMode() {
        String[] to = {"user@example.com"};
        String[] cc = {"invalid-email"};
        byte[] pdfBytes = "dummy-pdf-content".getBytes();

        emailservices.sendWithAttachment(to, cc, "Subject", "Body", pdfBytes, "report.pdf");

        verify(restTemplate, never()).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    void testSendWithAttachment_multipleRecipients_shouldSendOnce() {
        String[] to = {"user1@example.com", "user2@example.com"};
        String[] cc = {"cc1@example.com", "cc2@example.com"};
        byte[] pdfBytes = "dummy-pdf-content".getBytes();
        String subject = "Test Subject";
        String body = "Test Body";
        String filename = "report.pdf";

        ResponseEntity<String> mockResponse = new ResponseEntity<>("Mail Sent Successfully", HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(mockResponse);

        emailservices.sendWithAttachment(to, cc, subject, body, pdfBytes, filename);

        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    void testSendWithAttachment_runtimeException_shouldBeCaughtInTestMode() {
        String[] to = {"user@example.com"};
        String[] cc = {"cc@example.com"};
        byte[] pdfBytes = "dummy-pdf-content".getBytes();

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
            .thenThrow(new RuntimeException("Simulated failure"));

        assertDoesNotThrow(() ->
            emailservices.sendWithAttachment(to, cc, "Subject", "Body", pdfBytes, "report.pdf")
        );
    }
}


















/*package com.example.emailservicetest;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;

import com.example.emailservice.Emailservices;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailservicesTest {

    @InjectMocks
    private Emailservices emailservices;

    @Mock private JavaMailSender mailSender;
    @Mock private MimeMessage mimeMessage;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        emailservices.setTestMode(true);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testIsValidEmail_valid() {
        assertTrue(emailservices.isValidEmail("user@example.com"));
    }

    @Test
    void testIsValidEmail_invalid() {
        assertFalse(emailservices.isValidEmail("invalid-email"));
    }

    @Test
    void testSendWithAttachment_valid_shouldSend() {
        String[] recipients = {"user@example.com"};
        byte[] pdf = new byte[]{1, 2, 3};

        assertDoesNotThrow(() ->
            emailservices.sendWithAttachment(recipients, "Subject", "Body", pdf, "Report.pdf")
        );

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testSendWithAttachment_invalidEmail_shouldSkipInTestMode() {
        String[] recipients = {"invalid-email"};
        emailservices.sendWithAttachment(recipients, "Subject", "Body", new byte[]{1}, "Report.pdf");
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void testSendWithAttachment_nullAttachment_shouldStillSend() {
        String[] recipients = {"user@example.com"};

        assertDoesNotThrow(() ->
            emailservices.sendWithAttachment(recipients, "Subject", "Body", null, null)
        );

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testSendWithAttachment_runtimeException_shouldBeCaughtInTestMode() {
        String[] recipients = {"user@example.com"};
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Simulated failure"));

        assertDoesNotThrow(() ->
            emailservices.sendWithAttachment(recipients, "Subject", "Body", new byte[]{1}, "Report.pdf")
        );
    }

    @Test
    void testSendWithAttachment_multipleRecipients_shouldSendOnce() {
        String[] recipients = {"user1@example.com", "user2@example.com"};
        byte[] pdf = new byte[]{1, 2, 3};

        emailservices.sendWithAttachment(recipients, "Subject", "Body", pdf, "Report.pdf");

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}



















/*package com.example.sendexceldatatoemail;
import com.example.emailservice.Emailservices;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailservicesTest {

    @Mock private JavaMailSender mailSender;
    @Mock private MimeMessage mimeMessage;

    @InjectMocks private Emailservices emailservices;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testSendEmailWithAttachment_success() {
        emailservices.setTestMode(false);
        String[] recipients = {"test1@example.com", "test2@example.com"};
        byte[] pdfBytes = "PDF content".getBytes();

        assertDoesNotThrow(() ->
            emailservices.sendWithAttachment(recipients, "Subject", "Body", pdfBytes)
        );

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendEmailWithAttachment_messageSendingException_shouldSuppressInTestMode() {
        emailservices.setTestMode(true);
        String[] recipients = {"test@example.com"};

        doThrow(new MailSendException("Simulated failure"))
            .when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() ->
            emailservices.sendWithAttachment(recipients, "Subject", "Body", null)
        );
    }

    @Test
    void testSendEmailWithAttachment_invalidEncoding_shouldFallback() {
        emailservices.setTestMode(false);
        String[] recipients = {"test@example.com"};
        byte[] pdfBytes = "PDF".getBytes();

        assertDoesNotThrow(() ->
            emailservices.sendWithAttachment(recipients, "Subject", "Body", pdfBytes)
        );
    }

    @Test
    void testSendEmailWithAttachment_invalidEmail_shouldThrowInNormalMode() {
        emailservices.setTestMode(false);
        String[] recipients = {"invalid-email"};

        assertThrows(IllegalArgumentException.class, () ->
            emailservices.sendWithAttachment(recipients, "Subject", "Body", null)
        );
    }

    @Test
    void testSendEmailWithAttachment_invalidEmail_shouldSuppressInTestMode() {
        emailservices.setTestMode(true);
        String[] recipients = {"invalid-email"};

        assertDoesNotThrow(() ->
            emailservices.sendWithAttachment(recipients, "Subject", "Body", null)
        );
    }
}

*/













/*
 * package com.example.sendexceldatatoemail; import
 * com.example.emailservice.Emailservices;
 * 
 * import jakarta.mail.internet.MimeMessage;
 * 
 * import org.junit.jupiter.api.BeforeEach; import org.junit.jupiter.api.Test;
 * import org.mockito.*; import org.springframework.mail.MailSendException;
 * import org.springframework.mail.javamail.JavaMailSender;
 * 
 * import static org.junit.jupiter.api.Assertions.assertDoesNotThrow; import
 * static org.mockito.Mockito.*;
 * 
 * public class EmailservicesTest {
 * 
 * @Mock private JavaMailSender mailSender;
 * 
 * @Mock private MimeMessage mimeMessage;
 * 
 * @InjectMocks private Emailservices emailservices;
 * 
 * @BeforeEach void setup() { MockitoAnnotations.openMocks(this);
 * when(mailSender.createMimeMessage()).thenReturn(mimeMessage); }
 * 
 * @Test void testSendEmailWithAttachment_success() { String[] recipients =
 * {"test1@example.com", "test2@example.com"}; byte[] pdfBytes =
 * "PDF content".getBytes();
 * 
 * assertDoesNotThrow(() -> emailservices.sendWithAttachment(recipients,
 * "Subject", "Body", pdfBytes)); verify(mailSender).send(mimeMessage); }
 * 
 * @Test void
 * testSendEmailWithAttachment_messageSendingException_shouldCallback() {
 * String[] recipients = { "test@example.com" };
 * 
 * // ✅ Simulate failure inside mailSender doThrow(new
 * MailSendException("Simulated failure"))
 * .when(mailSender).send(any(MimeMessage.class));
 * 
 * // ✅ Call the real method and assert it doesn't throw assertDoesNotThrow(()
 * -> emailservices.sendWithAttachment(recipients, "Subject", "Body", null)); }
 * 
 * @Test void testSendEmailWithAttachment_invalidEncoding_shouldFallback() {
 * String[] recipients = {"test@example.com"}; byte[] pdfBytes =
 * "PDF".getBytes();
 * 
 * assertDoesNotThrow(() -> emailservices.sendWithAttachment(recipients,
 * "Subject", "Body", pdfBytes)); } }
 */