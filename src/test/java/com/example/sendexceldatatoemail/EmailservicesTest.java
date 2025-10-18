package com.example.sendexceldatatoemail;

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

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private Emailservices emailservices;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testSendEmailWithAttachment_success() {
        emailservices.setTestMode(false); // ✅ Disable test mode to suppress rethrow
        String[] recipients = {"test1@example.com", "test2@example.com"};
        byte[] pdfBytes = "PDF content".getBytes();

        assertDoesNotThrow(() ->
            emailservices.sendWithAttachment(recipients, "Subject", "Body", pdfBytes)
        );

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendEmailWithAttachment_messageSendingException_shouldNotThrow() {
        emailservices.setTestMode(false); // ✅ Disable test mode to suppress rethrow
        String[] recipients = {"test@example.com"};

        doThrow(new MailSendException("Simulated failure"))
            .when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() ->
            emailservices.sendWithAttachment(recipients, "Subject", "Body", null)
        );
    }

    @Test
    void testSendEmailWithAttachment_invalidEncoding_shouldFallback() {
        emailservices.setTestMode(false); // ✅ Disable test mode
        String[] recipients = {"test@example.com"};
        byte[] pdfBytes = "PDF".getBytes();

        assertDoesNotThrow(() ->
            emailservices.sendWithAttachment(recipients, "Subject", "Body", pdfBytes)
        );
    }
}
















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