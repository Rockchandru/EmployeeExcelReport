package com.example.emailscheduler;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.dto.EmployeeFloorSummary;
import com.example.emailservice.Emailservices;
import com.example.emailservice.ExcelReportGenerator;
import com.example.repo.EmployeeSwipeRepository;

@Component
public class EmailScheduler {

    @Autowired
    private Emailservices emailService;

    @Autowired
    private EmployeeSwipeRepository repository;

    @Autowired
    private ExcelReportGenerator excelGenerator;

    
    @Scheduled(cron = "0 33 17 * * ?") // Runs every day at 7:40 PM

    public void sendExcelMail() {
    	try {
    		//LocalDateTime start = LocalDate.now().atStartOfDay();
    		//LocalDateTime end = start.plusDays(1);
    		LocalDateTime start = LocalDateTime.of(2025,7, 18, 0, 0);
    		LocalDateTime end = LocalDateTime.of(2025,7, 19, 0, 0);


    		List<EmployeeFloorSummary> summaries = repository.getDailyFloorSummary(start, end);
    		if (summaries.isEmpty()) {
    			System.out.println("⚠️ No swipe data found for today.");
    			return;
    		}

    		byte[] report = excelGenerator.generateFloorReport(summaries);

    		String[] recipients = {
    				"rpprem04@gmail.com",
    				"haiiucedu@gmail.com"
    		};

    		emailService.sendEmailWithAttachment(
    				recipients,
    				"Daily Floor Swipe Summary",
    				"Please find the attached Excel report.",
    				report
    				);

    		System.out.println("📤 Scheduled email sent.");
    	} catch (Exception e) {
    		System.out.println("❌ Scheduler error: " + e.getMessage());
    		e.printStackTrace();
    	}
    }
}













































/*
 * import java.io.File; import java.time.LocalDate; import
 * java.time.LocalDateTime; import java.time.LocalTime; import java.util.List;
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.scheduling.annotation.Scheduled; import
 * org.springframework.stereotype.Component; import
 * com.example.dto.EmployeeFloorSummary; import
 * com.example.emailservice.Emailservices;
 * 
 * @Component public class EmailScheduler {
 * 
 * @Autowired private Emailservices emailService;
 * 
 * @Autowired private EmployeeSwipeRepository repository;
 * 
 * @Autowired private ExcelReportGenerator excelGenerator;
 * 
 * 
 * 
 * 
 * @Scheduled(cron = "0 0 15 * * ?") // Runs daily at 3 PM public String
 * sendExcelMail() { try { LocalDateTime start = LocalDate.now().atStartOfDay();
 * // 00:00 LocalDateTime end = start.plusDays(1); // 23:59:59
 * 
 * List<EmployeeFloorSummary> summaries = repository.getDailyFloorSummary(start,
 * end); String report = excelGenerator.generateFloorReport(summaries);
 * 
 * 
 * String[] recipients = { "chandrutvm13@gmail.com", "haiiucedu@gmail.com" };
 * 
 * emailService.sendEmailWithAttachment( recipients, "Excel Data",
 * "Please find the attached Excel file.", report // ✅ pass File directly );
 * 
 * System.out.println("📤 Email sent with Excel report."); return "Email sent!";
 * } catch (Exception e) { e.printStackTrace(); // ✅ log full error return
 * "Error: " + e.getMessage(); } }
 */
   /* @Scheduled(cron = "0 0 15   ?") // Runs daily at 3 PM
    public String sendExcelMail() {
        try {
            LocalTime start = LocalTime.of(0, 0);           // 00:00
            LocalTime end = LocalTime.of(23, 59, 59);        // 23:59:59

            List<EmployeeFloorSummary> summaries = repository.getDailyFloorSummary(start, end);
            File report = excelGenerator.generateFloorReport(summaries);
            String filePath = emailService.filePath(report);

      
            String[] recipients = {
                "chandrutvm13@gmail.com",
                "haiiucedu@gmail.com"
            };

            emailService.sendEmailWithAttachment(
                recipients,
                "Excel Data",
                "Please find the attached Excel file.",
                filePath
            );

            return "Email sent!";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

} 
    
    */
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	//@Scheduled(cron = "0 30 15 * * ?")
 //  @Scheduled(cron = "0 */03 * * * ?")
   /* public String sendExcelMail() {
    	try {
    		LocalDateTime start = LocalDate.now().atStartOfDay();
            LocalDateTime end = start.plusDays(1);

            List<EmployeeFloorSummary> summaries = repository.getDailyFloorSummary(start, end);
            File report = excelGenerator.generateFloorReport(summaries);
            emailService.filepath(report);
            
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    	*/
    	
        /*try {
            String filePath = excelService.getExcelPath();
            String[] recipients = {
                "chandrutvm13@gmail.com",
                "haiiucedu@gmail.com"
            };

            emailService.sendEmailWithAttachment(
                    recipients,
                    "Excel Data",
                    "Please find the attached Excel file.",
                    filePath
                );
            return "Email sent!";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }*/
    


