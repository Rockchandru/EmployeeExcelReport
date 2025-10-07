package com.example.emailscheduler;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

	/*
	 * @Value("${send.recipient3}") private String recipient3;
	 * 
	 * @Value("${send.recipient4") private String recipient4;
	 */

    @Scheduled(cron = "${send.email.expression}")
    public void sendPdfMail() {
        try {
            LocalDateTime start = LocalDateTime.of(2025, 10, 05, 0, 0);
            LocalDateTime end = start.plusDays(1);
            String location = "MVL";

            List<EmployeeFloorSummary> summaries = repository.getTowerWiseSummaryBetween(start, end, location);

            if (summaries.isEmpty()) {
                System.out.println("⚠️ No swipe data found for " + location + " between " + start + " and " + end);
                return;
            }
            /* recipient3 ,recipient4 */
            byte[] report = pdfReportGenerator.generateTowerSummaryPdf(summaries, start.toLocalDate());
			String[] recipients = { recipient1, recipient2,};
            
            for (String email : recipients) {
                if (!email.matches(".+@.+\\..+")) {
                    throw new IllegalArgumentException("Invalid email: " + email);
                }
            }


            String formattedDate = start.toLocalDate().format(DateTimeFormatter.ofPattern("d MMMM yyyy"));
            String subject = "RFID Punching Report – " + location + " Towers A–E – " + formattedDate;
            String body = "Dear Madam,<br><br>" +
                    
              "Please find attached the RFID Punching Report for <strong>" + location + "</strong>, covering Towers A to E for the date <strong>" + formattedDate + "</strong>.<br>" +
              "The report provides a detailed overview of employee activity segmented by tower, including individual punch counts and total summaries.<br><br>" +

              "<span style='font-size:14px; font-weight:bold;'>Noted:</span><br>" +
              "• <span style='color:red; font-weight:bold;'>Red</span> – Low punching activity<br>" +
              "• <span style='color:gray; font-weight:bold;'>Gray</span> – Average or normal punching activity<br><br>" +

              "Thank you.<br><br>" +
              "<span style='font-size:13px;'>Warm regards,</span><br>" +
              "<span style='font-size:13px;'>Chandru</span><br>" +
              "<span style='font-size:13px;'>Athulya Senior Care</span>";



            emailService.sendEmailWithAttachment(recipients, subject, body, report);
            System.out.println("📤 Scheduled email sent for " + formattedDate);
        } catch (Exception e) {
            System.out.println("❌ Scheduler error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
