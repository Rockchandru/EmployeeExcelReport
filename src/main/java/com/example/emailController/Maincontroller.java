package com.example.emailcontroller;
import com.example.dto.EmployeeFloorSummary;
import com.example.emailservice.Emailservices;
import com.example.pdfservice.PdfReportGenerator;
import com.example.repo.EmployeeSwipeRepository;
import com.example.emailscheduler.EmailScheduler;
import com.example.backup.SiteBackupScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api")
public class Maincontroller {

    private static final Logger logger = LoggerFactory.getLogger(Maincontroller.class);

    @Autowired private Emailservices emailService;
    @Autowired private EmployeeSwipeRepository repository;
    @Autowired private PdfReportGenerator pdfReportGenerator;
    @Autowired private EmailScheduler scheduler;
    @Autowired private SiteBackupScheduler backupScheduler;

    @GetMapping("/send-tower-summary")
    public String sendTowerSummaryEmail() {
        try {
            LocalDateTime start = LocalDateTime.of(2025, 10, 13, 0, 0);
            LocalDateTime end = start.plusDays(1);
            String location = "MVL";

            List<Object[]> rawResults = repository.getTowerWiseSummaryBetween(start, end, location);

            Set<String> seenKeys = new HashSet<>();
            List<EmployeeFloorSummary> summaries = rawResults.stream()
                .map(row -> new EmployeeFloorSummary((Integer) row[0], (String) row[1], (String) row[2], (String) row[3],
                                                     ((Number) row[4]).longValue(), ((Number) row[5]).longValue(),
                                                     ((Number) row[6]).longValue(), ((Number) row[7]).longValue(),
                                                     ((Number) row[8]).longValue()))
                .filter(s -> seenKeys.add(s.getEmployeeId() + "-" + s.getDesignation()))
                .toList();

            if (summaries.isEmpty()) {
                return "⚠️ No tower-wise data found for " + location + " on " + start.toLocalDate();
            }

            byte[] report = pdfReportGenerator.generateTowerSummaryPdf(summaries, start.toLocalDate());

            List<String> recipients = scheduler.getRecipients();
            for (String email : recipients) {
                if (email == null || !email.matches(".+@.+\\..+")) {
                    return "❌ Invalid email: " + email;
                }
            }

            String formattedDate = start.toLocalDate().format(DateTimeFormatter.ofPattern("d MMM yyyy"));
            String subject = "Tower-wise RFID Report – " + location + " – " + formattedDate;
            String body = "Dear Team,<br><br>" +
                "Please find attached the RFID Punching Report for <strong>" + location + "</strong> on <strong>" + formattedDate + "</strong>.<br><br>" +
                "Thank you,<br><span style='font-size:13px;'>Chandru</span>";

            emailService.sendWithAttachment(recipients.toArray(new String[0]), subject, body, report);
            return "✅ Tower-wise report for " + formattedDate + " sent successfully!";
        } catch (Exception e) {
            logger.error("Failed to generate or send report", e);
            return "❌ Failed to generate or send report: " + e.getMessage();
        }
    }
    // ✅ 1. Toggle cron job
    @PostMapping("/cron/toggle")
    public ResponseEntity<String> toggleCron(@RequestParam boolean enable) {
        scheduler.setCronEnabled(enable);
        return ResponseEntity.ok("Cron job " + (enable ? "enabled" : "disabled"));
    }

    // ✅ 2. Add recipient
    @PostMapping("/recipients/add")
    public ResponseEntity<String> addRecipient(@RequestParam String email) {
        scheduler.addRecipient(email);
        return ResponseEntity.ok("Added recipient: " + email);
    }

    // ✅ 3. Remove recipient
    @PostMapping("/recipients/remove")
    public ResponseEntity<String> removeRecipient(@RequestParam String email) {
        scheduler.removeRecipient(email);
        return ResponseEntity.ok("Removed recipient: " + email);
    }
    @GetMapping("/recipients")
    public ResponseEntity<List<String>> listRecipients() {
        return ResponseEntity.ok(scheduler.getRecipients());
    }

    

    // ✅ 4. Retrieve records by date
    @GetMapping("/summary")
    public ResponseEntity<List<EmployeeFloorSummary>> getSummary(@RequestParam String date) {
        LocalDateTime start = LocalDate.parse(date).atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Object[]> raw = repository.getTowerWiseSummaryBetween(start, end, "MVL");

        Set<String> seenKeys = new HashSet<>();
        List<EmployeeFloorSummary> summaries = raw.stream()
            .map(row -> new EmployeeFloorSummary((Integer) row[0], (String) row[1], (String) row[2], (String) row[3],
                                                 ((Number) row[4]).longValue(), ((Number) row[5]).longValue(),
                                                 ((Number) row[6]).longValue(), ((Number) row[7]).longValue(),
                                                 ((Number) row[8]).longValue()))
            .filter(s -> seenKeys.add(s.getEmployeeId() + "-" + s.getDesignation()))
            .toList();

        return ResponseEntity.ok(summaries);
    }

    // ✅ 5. Generate CSV report without cron
    @GetMapping("/summary/csv")
    public ResponseEntity<byte[]> getCsvReport(@RequestParam String date) {
        LocalDateTime start = LocalDate.parse(date).atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Object[]> raw = repository.getTowerWiseSummaryBetween(start, end, "MVL");

        Set<String> seenKeys = new HashSet<>();
        List<EmployeeFloorSummary> summaries = raw.stream()
            .map(row -> new EmployeeFloorSummary((Integer) row[0], (String) row[1], (String) row[2], (String) row[3],
                                                 ((Number) row[4]).longValue(), ((Number) row[5]).longValue(),
                                                 ((Number) row[6]).longValue(), ((Number) row[7]).longValue(),
                                                 ((Number) row[8]).longValue()))
            .filter(s -> seenKeys.add(s.getEmployeeId() + "-" + s.getDesignation()))
            .toList();

        StringBuilder sb = new StringBuilder("S.No,Employee ID,Name,Designation,A,B,C,D,E,Total\n");
        int i = 1;
        for (EmployeeFloorSummary s : summaries) {
            sb.append(i++).append(",").append(s.getEmployeeId()).append(",").append(s.getEmployeeName()).append(",")
              .append(s.getDesignation()).append(",").append(s.getTowerA()).append(",").append(s.getTowerB()).append(",")
              .append(s.getTowerC()).append(",").append(s.getTowerD()).append(",").append(s.getTowerE()).append(",")
              .append(s.getTotal()).append("\n");
        }

        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=report.csv")
            .contentType(MediaType.TEXT_PLAIN)
            .body(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    // ✅ 6. Trigger site backup manually
    @PostMapping("/backup/trigger")
    public ResponseEntity<String> triggerBackup() {
        String result = backupScheduler.triggerBackupManually();
        return ResponseEntity.ok(result);
    }
}


















/*
    @GetMapping("/backup/status")
    public ResponseEntity<String> getBackupStatus() {
        return ResponseEntity.ok("🕒 Last backup: " + backupScheduler.getLastBackupTime());
    }

    @PostMapping("/backup/trigger")
    public ResponseEntity<String> triggerBackup() {
        String result = backupScheduler.triggerBackupManually();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/cron/toggle")
    public ResponseEntity<String> toggleCron(@RequestParam boolean enable) {
        scheduler.setCronEnabled(enable);
        return ResponseEntity.ok("Cron job " + (enable ? "enabled" : "disabled"));
    }

    @PostMapping("/recipients/add")
    public ResponseEntity<String> addRecipient(@RequestParam String email) {
        scheduler.addRecipient(email);
        return ResponseEntity.ok("Added: " + email);
    }

    @PostMapping("/recipients/remove")
    public ResponseEntity<String> removeRecipient(@RequestParam String email) {
        scheduler.removeRecipient(email);
        return ResponseEntity.ok("Removed: " + email);
    }

    @GetMapping("/recipients")
    public ResponseEntity<List<String>> listRecipients() {
        return ResponseEntity.ok(scheduler.getRecipients());
    }

    @GetMapping("/summary")
    public ResponseEntity<List<EmployeeFloorSummary>> getSummary(@RequestParam String date) {
        LocalDateTime start = LocalDate.parse(date).atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Object[]> raw = repository.getTowerWiseSummaryBetween(start, end, "MVL");

        List<EmployeeFloorSummary> summaries = raw.stream().map(row -> {
            EmployeeFloorSummary s = new EmployeeFloorSummary((Integer) row[0], (String) row[1], (String) row[2], (String) row[3],
                                                              ((Number) row[4]).longValue(), ((Number) row[5]).longValue(),
                                                              ((Number) row[6]).longValue(), ((Number) row[7]).longValue(),
                                                              ((Number) row[8]).longValue());
            s.setTotal(s.getTowerA() + s.getTowerB() + s.getTowerC() + s.getTowerD() + s.getTowerE());
            return s;
        }).toList();

        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/summary/csv")
    public ResponseEntity<byte[]> getCsvReport(@RequestParam String date) {
        LocalDateTime start = LocalDate.parse(date).atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Object[]> raw = repository.getTowerWiseSummaryBetween(start, end, "MVL");

        List<EmployeeFloorSummary> summaries = raw.stream().map(row -> {
            EmployeeFloorSummary s = new EmployeeFloorSummary((Integer) row[0], (String) row[1], (String) row[2], (String) row[3],
                                                              ((Number) row[4]).longValue(), ((Number) row[5]).longValue(),
                                                              ((Number) row[6]).longValue(), ((Number) row[7]).longValue(),
                                                              ((Number) row[8]).longValue());
            s.setTotal(s.getTowerA() + s.getTowerB() + s.getTowerC() + s.getTowerD() + s.getTowerE());
            return s;
        }).toList();

        StringBuilder sb = new StringBuilder("S.No,Employee ID,Name,Designation,A,B,C,D,E,Total\n");
        int i = 1;
        for (EmployeeFloorSummary s : summaries) {
            sb.append(i++).append(",").append(s.getEmployeeId()).append(",").append(s.getEmployeeName()).append(",")
              .append(s.getDesignation()).append(",").append(s.getTowerA()).append(",").append(s.getTowerB()).append(",")
              .append(s.getTowerC()).append(",").append(s.getTowerD()).append(",").append(s.getTowerE()).append(",")
              .append(s.getTotal()).append("\n");
        }

        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=report.csv")
            .contentType(MediaType.TEXT_PLAIN)
            .body(sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
*/
