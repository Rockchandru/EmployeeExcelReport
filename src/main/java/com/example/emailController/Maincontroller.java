package com.example.emailcontroller;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.dto.EmployeeFloorSummary;
import com.example.emailservice.Emailservices;
import com.example.pdfservice.PdfReportGenerator;
import com.example.repo.EmployeeSwipeRepository;

@RestController
@RequestMapping("/api")
public class Maincontroller {

    private static final Logger logger = LoggerFactory.getLogger(Maincontroller.class);

    static {
        logger.info("Maincontroller initialized: handling /api/send-tower-summary endpoint");
    }

    @Autowired
    private Emailservices emailService;

    @Autowired
    private EmployeeSwipeRepository repository;

    @Autowired
    private PdfReportGenerator pdfReportGenerator;

    @GetMapping("/send-tower-summary")
    public String sendTowerSummaryEmail() {
        logger.info("Tower summary email endpoint triggered.");

        try {
			
			
			  LocalDateTime start = LocalDateTime.of(2025, 10, 12, 0, 0); LocalDateTime end
			  = start.plusDays(1); logger.debug("Report window: start={}, end={}", start,
			  end); String location="Pallavaram";
			  
			 
			
			
			/*
			 * LocalDateTime start =
			 * LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).
			 * withNano(0); LocalDateTime end = start.plusDays(1);
			 * logger.debug("Report window: start={}, end={}", start, end); String location
			 * = "MVL";
			 */
			 

            logger.debug("Fetching tower-wise summary for location={} between {} and {}", location, start, end);
          //  List<EmployeeFloorSummary> summaries = repository.getTowerWiseSummaryBetween(start, end, location);

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
                logger.warn("No tower-wise data found for {} on {}", location, start.toLocalDate());
                return "⚠️ No tower-wise data found for " + location + " on " + start.toLocalDate();
            }

            logger.info("Generating PDF report for {} records.", summaries.size());
            byte[] report = pdfReportGenerator.generateTowerSummaryPdf(summaries, start.toLocalDate());
			/*
			 * "priyatharshini.m@athulyaseniorcare.com", "prasannag@athulyaseniorcare.com",
			 * "senthil@athulyaseniorcare.com",
			 */
            String[] recipients = {
                "rpprem04@gmail.com",
               "haisenthil1975@gmail.com",
                "mageshema1180@gmail.com"
            };

            String formattedDate = start.toLocalDate().format(DateTimeFormatter.ofPattern("d MMM yyyy"));
            String subject = "Tower-wise RFID Report – " + location + " – " + formattedDate;
            String body = "Dear Team,<br><br>" +
            	    "Please find attached the RFID Punching Report for <strong>" + location + "</strong>, covering Towers A to E for <strong>" + formattedDate + "</strong>.<br><br>" +
            	    "This report provides a detailed overview of employee swipe activity segmented by tower, including individual punch counts and total summaries.<br><br>" +
            	    "<strong>Note:</strong><br>" +
            	    "• <span style='color:red; font-weight:bold;'>Red</span> – Low punching activity<br>" +
            	    "• <span style='color:gray; font-weight:bold;'>Gray</span> – Normal or average punching activity<br><br>" +
            	    "Thank you,<br>" +
            	    "<span style='font-size:13px;'>Chandru</span>";


            logger.info("Sending email to recipients: {}", String.join(", ", recipients));
            emailService.sendWithAttachment(recipients, subject, body, report);

            logger.info("Tower-wise report for {} sent successfully.", formattedDate);
            return "✅ Tower-wise report for " + formattedDate + " sent successfully!";
        } catch (Exception e) {
            logger.error("Failed to generate or send tower-wise report", e);
            return "❌ Failed to generate or send report: " + e.getMessage();
        }
    }
}
