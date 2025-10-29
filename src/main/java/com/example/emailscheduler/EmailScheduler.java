package com.example.emailscheduler;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.emailservice.Emailservices;
import com.example.pdfservice.PdfReportGenerator;
import com.example.repo.EmployeeSwipeRepository;
import com.example.dto.EmployeeFloorSummary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

@Component
public class EmailScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EmailScheduler.class);

    private final Emailservices emailService;
    private final EmployeeSwipeRepository repository;
    private final PdfReportGenerator pdfReportGenerator;

    private final List<String> recipients = new CopyOnWriteArrayList<>();
    private final List<String> ccRecipients = new CopyOnWriteArrayList<>();
    private volatile boolean cronEnabled = true;
    private boolean testMode = false;

    @Value("${send.recipient1}") private String r1;
    @Value("${send.recipient.cc1}") private String cc1;
    @Value("${send.recipient.cc2}") private String cc2;
    @Value("${send.recipient.cc3}") private String cc3;
    @Value("${send.recipient.cc4}") private String cc4;
    @Value("${send.recipient.cc5}") private String cc5;
    @Value("${send.recipient.cc6}") private String cc6;
    @Value("${send.recipient.cc7}") private String cc7;
   @Value("${send.recipient.cc8}") private String cc8;

    public EmailScheduler(Emailservices emailService, EmployeeSwipeRepository repository,
                          PdfReportGenerator pdfReportGenerator) {
        this.emailService = emailService;
        this.repository = repository;
        this.pdfReportGenerator = pdfReportGenerator;
    }
//cc4, cc5, cc6, cc7, cc8
    @PostConstruct
    public void loadRecipients() {
        recipients.addAll(Stream.of(r1).filter(email -> email != null && !email.isBlank()).toList());
        ccRecipients.addAll(Stream.of(cc1, cc2, cc3,cc4, cc5, cc6, cc7, cc8)
            .filter(email -> email != null && !email.isBlank())
            .toList());
        logger.info("📧 TO Recipients loaded: {}", String.join(", ", recipients));
        logger.info("📧 CC Recipients loaded: {}", String.join(", ", ccRecipients));
    }

    public List<String> getRecipients() { return recipients; }
    public List<String> getCcRecipients() { return ccRecipients; }

    public void addRecipient(String email) {
        if (email != null && email.matches(".+@.+\\..+")) {
            recipients.add(email);
            logger.info("✅ TO recipient added: {}", email);
        } else {
            logger.warn("❌ Invalid TO format: {}", email);
        }
    }

    public void removeRecipient(String email) {
        if (email != null && recipients.remove(email)) {
            logger.info("🗑️ TO recipient removed: {}", email);
        } else {
            logger.warn("⚠️ TO recipient not found or invalid: {}", email);
        }
    }

    public void clearRecipients() {
        recipients.clear();
        logger.info("🧹 All TO recipients cleared.");
    }

    public void addCc(String email) {
        if (email != null && email.matches(".+@.+\\..+")) {
            ccRecipients.add(email);
            logger.info("✅ CC recipient added: {}", email);
        } else {
            logger.warn("❌ Invalid CC format: {}", email);
        }
    }

    public void removeCc(String email) {
        if (email != null && ccRecipients.remove(email)) {
            logger.info("🗑️ CC recipient removed: {}", email);
        } else {
            logger.warn("⚠️ CC recipient not found or invalid: {}", email);
        }
    }

    public void clearCcRecipients() {
        ccRecipients.clear();
        logger.info("🧹 All CC recipients cleared.");
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public void setCronEnabled(boolean enabled) {
        this.cronEnabled = enabled;
    }

    public boolean isCronEnabled() {
        return cronEnabled;
    }

    @Scheduled(cron = "${send.email.expression}")
    public void sendPdfMail() {
        logger.info("📅 [START] Scheduled email job triggered");
        if (!cronEnabled) {
            logger.info("⏸️ Cron job is disabled. Skipping execution.");
            return;
        }

        try {
			/*
			 * LocalDateTime start = LocalDateTime.of(2025, 10, 28, 0, 0); LocalDateTime end
			 * = start.plusDays(1); String location = "MVL";
			 */
        	
        	LocalDateTime start = LocalDateTime.now()
        		    .minusDays(1)
        		    .withHour(0).withMinute(0).withSecond(0).withNano(0);
        		LocalDateTime end = start.plusDays(1);
        		String location = "MVL";
        		logger.debug("📅 Report window: start={}, end={}", start, end);
        		
        		
            List<Object[]> rawResults = repository.getTowerWiseSummaryBetween(start, end, location);
            if (rawResults == null || rawResults.isEmpty()) {
                logger.warn("⚠️ No swipe data found for {} between {} and {}", location, start, end);
                return;
            }

            Set<String> seenKeys = new HashSet<>();
            List<EmployeeFloorSummary> summaries = rawResults.stream()
                .map(row -> new EmployeeFloorSummary((Integer) row[0], (String) row[1], (String) row[2], (String) row[3],
                    ((Number) row[4]).longValue(), ((Number) row[5]).longValue(),
                    ((Number) row[6]).longValue(), ((Number) row[7]).longValue(),
                    ((Number) row[8]).longValue()))
                .filter(s -> seenKeys.add(s.getEmployeeId() + "-" + s.getDesignation()))
                .toList();

            if (summaries.isEmpty()) {
                logger.warn("⚠️ No deduplicated summaries to report for {}", location);
                return;
            }

            byte[] report = pdfReportGenerator.generateTowerSummaryPdf(summaries, start.toLocalDate());
            logger.info("📄 PDF report generated");

            String formattedDate = start.toLocalDate().format(DateTimeFormatter.ofPattern("d MMM yyyy"));
            String subject = "Tower-wise RFID Report – " + location + " – " + formattedDate;
            String body = "Dear Team,<br>" +
                "Please find the RFID Punching Report for <strong>" + location + "</strong>, covering Towers A to E for <strong>" + formattedDate + "</strong>.<br>" +
                "This report provides a detailed overview of employee swipe activity segmented by tower.<br><br>" +
                "<strong>Note:</strong><br>" +
                "• <span style='color:red; font-weight:bold;'>Red</span> – Low punching activity<br>" +
                "• <span style='color:gray; font-weight:bold;'>Gray</span> – Normal or average punching activity<br><br>" +
                "Thank you,<br><span style='font-size:13px;'>Chandru</span>";

            emailService.sendWithAttachment(
                    recipients.toArray(new String[0]),
                    ccRecipients.toArray(new String[0]),
                    subject,
                    body,
                    report,
                    "Tower_Report_" + formattedDate + ".pdf"
                );

                logger.info("📤 Scheduled email sent successfully for {}", formattedDate);

            } catch (Exception e) {
                logger.error("❌ Error during scheduled email execution: {}", e.getMessage(), e);
                if (testMode) throw new RuntimeException(e);
            }

            logger.info("📅 [END] Scheduled email job completed");
        }
    }





































