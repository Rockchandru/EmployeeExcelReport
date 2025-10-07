package com.example.emailController;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.dto.EmployeeFloorSummary;
import com.example.emailservice.Emailservices;
import com.example.pdfservice.PdfReportGenerator;
import com.example.repo.EmployeeSwipeRepository;

@RestController
@RequestMapping("/api")
public class Maincontroller {

    @Autowired
    private Emailservices emailService;

    @Autowired
    private EmployeeSwipeRepository repository;

    @Autowired
    private PdfReportGenerator pdfReportGenerator;

    @GetMapping("/send-tower-summary")
    public String sendTowerSummaryEmail() {
        try {
            LocalDateTime start = LocalDateTime.of(2025, 10, 05, 0, 0);
            LocalDateTime end = start.plusDays(1);
            String location = "MVL";

            List<EmployeeFloorSummary> summaries = repository.getTowerWiseSummaryBetween(start, end, location);

            if (summaries.isEmpty()) {
                return "⚠️ No tower-wise data found for " + location + " on " + start.toLocalDate();
            }

            byte[] report = pdfReportGenerator.generateTowerSummaryPdf(summaries, start.toLocalDate());

            /*
			 * "rpprem4@gmail.com", "priyatharshini.m@athulyaseniorcare.com",
			 */
            
            String[] recipients = {
				
            		"prasannag@athulyaseniorcare.com",
                "senthil@athulyaseniorcare.com"
            };

            String formattedDate = start.toLocalDate().format(DateTimeFormatter.ofPattern("d MMM yyyy"));
            String subject = "Tower-wise RFID Report – " + location + " – " + formattedDate;
            String body = "Good day team,<br><br>" +
                          "Please find the attached Tower-wise RFID Punching Report for " + location + " on " + formattedDate + ".<br><br>" +
                          "Regards,<br>Admin";

            emailService.sendEmailWithAttachment(recipients, subject, body, report);
            return "✅ Tower-wise report for " + formattedDate + " sent successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Failed to generate or send report: " + e.getMessage();
        }
    }
}
