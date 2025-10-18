package com.example.emailscheduler;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.dto.EmployeeFloorSummary;
import com.example.emailservice.Emailservices;
import com.example.pdfservice.PdfReportGenerator;
import com.example.repo.EmployeeSwipeRepository;

@Component
public class EmailScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EmailScheduler.class);

    static {
        logger.info("EmailScheduler initialized: scheduled task for sending tower-wise PDF reports is active.");
    }

    @Autowired
    private Emailservices emailService;

    @Autowired
    private EmployeeSwipeRepository repository;

    @Autowired
    private PdfReportGenerator pdfReportGenerator;

    @Value("${send.recipient1}")
    private String recipient1;

    @Value("${send.recipient2}")
    private String recipient2;

    @Value("${send.recipient3}")
    private String recipient3;

    // ✅ Public setters for testing (no impact on production logic)
    
    private boolean testMode = false;

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }
    public void setRecipient1(String recipient1) {
        this.recipient1 = recipient1;
    }

    public void setRecipient2(String recipient2) {
        this.recipient2 = recipient2;
    }

    public void setRecipient3(String recipient3) {
        this.recipient3 = recipient3;
    }

    @Scheduled(cron = "${send.email.expression}")
    public void sendPdfMail() throws Exception {
        logger.info("Scheduled task triggered: Preparing to send tower-wise PDF report email.");

        try {
        	
			/*
			 * LocalDateTime start =
			 * LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).
			 * withNano(0); LocalDateTime end = start.plusDays(1);
			 * logger.debug("Report window: start={}, end={}", start, end); String location
			 * = "MVL";
			 */
        	
				
				  LocalDateTime start = LocalDateTime.of(2025, 10, 12, 0, 0); LocalDateTime end
				  = start.plusDays(1); logger.debug("Report window: start={}, end={}", start,
				  end); String location = "Pallavaram";
				  logger.debug("Report window: start={}, end={}", start, end);
				 
            logger.debug("Fetching swipe summary for location={} between {} and {}", location, start, end);
            //List<EmployeeFloorSummary> summaries = repository.getTowerWiseSummaryBetween(start, end, location);
            
            List<Object[]> rawResults = repository.getTowerWiseSummaryBetween(start, end, location);

            List<EmployeeFloorSummary> summaries = rawResults.stream().map(row -> {
                return new EmployeeFloorSummary(
                    (Integer) row[0],               // sNo
                    (String) row[1],                // employeeId
                    (String) row[2],                // employeeName
                    (String) row[3],                // designation
                    ((Number) row[4]).longValue(),  // towerA
                    ((Number) row[5]).longValue(),  // towerB
                    ((Number) row[6]).longValue(),  // towerC
                    ((Number) row[7]).longValue(),  // towerD
                    ((Number) row[8]).longValue()   // towerE
                );
            }).toList();


            if (summaries.isEmpty()) {
                logger.warn("No swipe data found for {} between {} and {}", location, start, end);
                return;
            }

            logger.info("Generating PDF report for {} records.", summaries.size());
            byte[] report = pdfReportGenerator.generateTowerSummaryPdf(summaries, start.toLocalDate());

            String[] recipients = { recipient1, recipient2, recipient3 };

            for (String email : recipients) {
                if (email == null || !email.matches(".+@.+\\..+")) {
                    logger.error("❌ Invalid email address: {}", email);
                    if (testMode) {
                        throw new IllegalArgumentException("Invalid email: " + email);
                    }
                    return;
                }
            }

        
            String formattedDate = start.toLocalDate().format(DateTimeFormatter.ofPattern("d MMMM yyyy"));
            String subject = "RFID Punching Report – " + location + " Towers A–E – " + formattedDate;
            String body = "Dear Team,<br>" +
            	    "Please find attached the RFID Punching Report for <strong>" + location + "</strong>, covering Towers A to E for <strong>" + formattedDate + "</strong>.<br>" +
            	    "This report provides a detailed overview of employee swipe activity segmented by tower, including individual punch counts and total summaries.<br><br>" +
            	    "<strong>Note:</strong><br>" +
            	    "• <span style='color:red; font-weight:bold;'>Red</span> – Low punching activity<br>" +
            	    "• <span style='color:gray; font-weight:bold;'>Gray</span> – Normal or average punching activity<br><br>" +
            	    "Thank you,<br>" +
            	    "<span style='font-size:13px;'>Chandru</span>";



            logger.info("Sending email to recipients: {}", String.join(", ", recipients));
            emailService.sendWithAttachment(recipients, subject, body, report);

            logger.info("📤 Scheduled email sent successfully for {}", formattedDate);
            
        }catch (Exception e) {
            logger.error("❌ Scheduler error occurred while sending report", e);
            if (testMode) {
                throw e; // ✅ Rethrow so test can catch it
            }
        }
}
}
