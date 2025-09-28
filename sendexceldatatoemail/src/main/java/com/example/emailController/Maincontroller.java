package com.example.emailController;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.dto.EmployeeFloorSummary;
import com.example.emailservice.Emailservices;
import com.example.emailservice.ExcelReportGenerator;
import com.example.repo.EmployeeSwipeRepository;

@RestController
@RequestMapping("/api")
public class Maincontroller {

    @Autowired
    private Emailservices emailService;

    @Autowired
    private EmployeeSwipeRepository repository;

    @Autowired
    private ExcelReportGenerator excelGenerator;

    @GetMapping("/send")
    public String sendExcelMail() {
        try {
            //LocalDateTime start = LocalDate.now().atStartOfDay();
            //LocalDateTime end = start.plusDays(1);
            LocalDateTime start = LocalDateTime.of(2025, 9, 22, 0, 0);
        	LocalDateTime end = LocalDateTime.of(2025, 9, 23, 0, 0);

            List<EmployeeFloorSummary> summaries = repository.getDailyFloorSummary(start, end);
            if (summaries.isEmpty()) {
                return "⚠️ No swipe data found for today.";
            }

            byte[] report = excelGenerator.generateFloorReport(summaries);

            String[] recipients = {
                "rpprem04@gmail.com",
                "haiiucedu@gmail.com"
            };

            emailService.sendEmailWithAttachment(
                recipients,
                "Floor summary report",
                "Please find the attached Excel file.",
                report
            );

            return "✅ Email sent successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error: " + e.getMessage();
        }
    }
}





























































/*
 * import org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.RequestMapping; import
 * org.springframework.web.bind.annotation.RestController;
 * 
 * import java.io.File;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.core.io.FileSystemResource;
 * 
 * import com.example.emailservice.Emailservices;
 * 
 * import jakarta.annotation.PostConstruct;
 * 
 * @RestController
 * 
 * @RequestMapping("/api") public class Maincontroller {
 * 
 * @Autowired private Emailservices emailService;
 * 
 * @PostConstruct public void init() {
 * System.out.println("Maincontroller loaded successfully."); }
 * 
 * @GetMapping("/send") public String sendExcelMail() { try { String filePath =
 * emailService.getExcelPath(); File file = new File(filePath);
 * 
 * if (!file.exists()) { return "Error: Excel file not found at " + filePath; }
 * 
 * String[] recipients = { "chandrutvm13@gmail.com", "haiiucedu@gmail.com" };
 * 
 * emailService.sendEmailWithAttachment( recipients, "Excel Data",
 * "Please find the attached Excel file.", file );
 * 
 * return "✅ Email sent!"; } catch (Exception e) { e.printStackTrace(); // ✅ log
 * full error return "Error: " + e.getMessage(); } } }
 */

/*@RestController
@RequestMapping("/api")
public class Maincontroller {

    @Autowired
    private Emailservices emailService;

    @Autowired
    private Excelservices excelService;
    
    @PostConstruct
    public void init() {
        System.out.println("Maincontroller loaded successfully.");
    }

    @GetMapping("/send")
    public String sendExcelMail() {
        try {
            String filePath = excelService.getExcelPath();
            String[] recipients = {
                "chandrutvm13@gmail.com",
                "haiiucedu@gmail.com"
            };
            //FileSystemResource file = new FileSystemResource(new File(filePath));
            emailService.sendEmailWithAttachment(
                recipients,
                "Excel Data",
                "Please find the attached Excel file.",
                new File(filePath)
            );
            return "Email sent!";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }}
    */
