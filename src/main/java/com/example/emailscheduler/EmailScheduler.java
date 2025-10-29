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
	private volatile boolean cronEnabled = true;
	private boolean testMode = false;

	@Value("${send.recipient1}")
	private String r1;
	@Value("${send.recipient2}")
	private String r2;
	@Value("${send.recipient3}")
	private String r3;
	@Value("${send.recipient4}")
	private String r4;
	
	//@Value("${send.recipient5}")
	//private String r5;
	
	//@Value("${send.recipient6}")
	//private String r6;
	
	//@Value("${send.recipient7}")
	//private String r7;
	

	public EmailScheduler(Emailservices emailService, EmployeeSwipeRepository repository,
			PdfReportGenerator pdfReportGenerator) {
		this.emailService = emailService;
		this.repository = repository;
		this.pdfReportGenerator = pdfReportGenerator;
	}

	@PostConstruct
	public void loadRecipients() {
		recipients.addAll(Stream.of(r1, r2, r3,r4).filter(email -> email != null && !email.isBlank()).toList());
		logger.info("📧 Recipients loaded: {}", String.join(", ", recipients));
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

	public List<String> getRecipients() {
		return recipients;
	}

	public void addRecipient(String email) {
		if (email != null && email.matches(".+@.+\\..+")) {
			recipients.add(email);
			logger.info("✅ Recipient added: {}", email);
		} else {
			logger.warn("❌ Invalid recipient format: {}", email);
		}
	}

	public void removeRecipient(String email) {
		recipients.remove(email);
		logger.info("🗑️ Recipient removed: {}", email);
	}

	@Scheduled(cron = "${send.email.expression}")
	public void sendPdfMail() {
	    logger.info("📅 [START] Scheduled email job triggered");
	    if (!cronEnabled) {
	        logger.info("⏸️ Cron job is disabled. Skipping execution.");
	        return;
	    }

	    try {
	        LocalDateTime start = LocalDateTime.of(2025, 10, 15, 0, 0);
	        LocalDateTime end = start.plusDays(1);
	        String location = "MVL";
	        logger.debug("Report window: start={}, end={}", start, end);

	        /*
			 * LocalDateTime start =
			 * LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
			 * LocalDateTime end = start.plusDays(1); String location = "MVL";
			 */
            //logger.debug("Report window: start={}, end={}", start, end);


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

	        if (recipients.isEmpty()) {
	            logger.warn("⚠️ No recipients configured. Skipping email.");
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

	        String encodedPdf = Base64.getEncoder().encodeToString(report);
	        emailService.sendWithAttachment(recipients.toArray(new String[0]), subject, body, encodedPdf, "Tower_Report_" + formattedDate + ".pdf");

	        logger.info("📤 Scheduled email sent successfully for {}", formattedDate);

	    } catch (Exception e) {
	        logger.error("❌ Error during scheduled email execution: {}", e.getMessage(), e);
	        if (testMode) throw new RuntimeException(e);
	    }
	    logger.info("📅 [END] Scheduled email job completed");
	}
}







































/*
 * package com.example.emailscheduler; import jakarta.annotation.PostConstruct;
 * import org.slf4j.Logger; import org.slf4j.LoggerFactory; import
 * org.springframework.beans.factory.annotation.Value; import
 * org.springframework.scheduling.annotation.Scheduled; import
 * org.springframework.stereotype.Component;
 * 
 * import com.example.dto.EmployeeFloorSummary; import
 * com.example.emailservice.Emailservices; import
 * com.example.pdfservice.PdfReportGenerator; import
 * com.example.repo.EmployeeSwipeRepository;
 * 
 * import java.time.LocalDateTime; import java.time.format.DateTimeFormatter;
 * import java.util.*; import java.util.concurrent.CopyOnWriteArrayList; import
 * java.util.stream.Stream;
 * 
 * @Component public class EmailScheduler {
 * 
 * private static final Logger logger =
 * LoggerFactory.getLogger(EmailScheduler.class);
 * 
 * private final Emailservices emailService; private final
 * EmployeeSwipeRepository repository; private final PdfReportGenerator
 * pdfReportGenerator;
 * 
 * private final List<String> recipients = new CopyOnWriteArrayList<>(); private
 * volatile boolean cronEnabled = true; private boolean testMode = false;
 * 
 * @Value("${send.recipient1}") private String r1;
 * 
 * @Value("${send.recipient2}") private String r2;
 * 
 * @Value("${send.recipient3}") private String r3;
 * 
 * public EmailScheduler(Emailservices emailService, EmployeeSwipeRepository
 * repository, PdfReportGenerator pdfReportGenerator) { this.emailService =
 * emailService; this.repository = repository; this.pdfReportGenerator =
 * pdfReportGenerator; }
 * 
 * @PostConstruct public void loadRecipients() { recipients.addAll(Stream.of(r1,
 * r2, r3) .filter(email -> email != null && !email.isBlank()) .toList());
 * logger.info("📧 Recipients loaded: {}", String.join(", ", recipients)); }
 * 
 * public void setTestMode(boolean testMode) { this.testMode = testMode; }
 * 
 * public void setCronEnabled(boolean enabled) { this.cronEnabled = enabled; }
 * 
 * public boolean isCronEnabled() { return cronEnabled; }
 * 
 * public List<String> getRecipients() { return recipients; }
 * 
 * public void addRecipient(String email) { if (email != null &&
 * email.matches(".+@.+\\..+")) { recipients.add(email);
 * logger.info("✅ Recipient added: {}", email); } else {
 * logger.warn("❌ Invalid recipient format: {}", email); } }
 * 
 * public void removeRecipient(String email) { recipients.remove(email);
 * logger.info("🗑️ Recipient removed: {}", email); }
 * 
 * @Scheduled(cron = "${send.email.expression}") public void sendPdfMail() {
 * logger.info("📅 [START] Scheduled email job triggered"); if (!cronEnabled) {
 * logger.info("⏸️ Cron job is disabled. Skipping execution."); return; }
 * 
 * try { /* LocalDateTime start = LocalDateTime.of(2025, 10, 14, 0, 0);
 * LocalDateTime end = start.plusDays(1); String location = "MVL";
 * logger.debug("Report window: start={}, end={}", start, end);
 */
/*
 * LocalDateTime start =
 * LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
 * LocalDateTime end = start.plusDays(1); String location = "MVL";
 * logger.debug("Report window: start={}, end={}", start, end);
 * 
 * List<Object[]> rawResults = repository.getTowerWiseSummaryBetween(start, end,
 * location); if (rawResults == null || rawResults.isEmpty()) {
 * logger.warn("⚠️ No swipe data found for {} between {} and {}", location,
 * start, end); return; }
 * 
 * Set<String> seenKeys = new HashSet<>(); List<EmployeeFloorSummary> summaries
 * = rawResults.stream() .map(row -> new EmployeeFloorSummary((Integer) row[0],
 * (String) row[1], (String) row[2], (String) row[3], ((Number)
 * row[4]).longValue(), ((Number) row[5]).longValue(), ((Number)
 * row[6]).longValue(), ((Number) row[7]).longValue(), ((Number)
 * row[8]).longValue())) .filter(s -> seenKeys.add(s.getEmployeeId() + "-" +
 * s.getDesignation())) .toList();
 * 
 * if (summaries.isEmpty()) {
 * logger.warn("⚠️ No deduplicated summaries to report for {}", location);
 * return; }
 * 
 * if (recipients.isEmpty()) {
 * logger.warn("⚠️ No recipients configured. Skipping email."); return; }
 * 
 * for (String email : recipients) { if (email == null ||
 * !email.matches(".+@.+\\..+")) { logger.error("❌ Invalid email address: {}",
 * email); if (testMode) throw new IllegalArgumentException("Invalid email: " +
 * email); return; } }
 * 
 * byte[] report = pdfReportGenerator.generateTowerSummaryPdf(summaries,
 * start.toLocalDate()); logger.info("📄 PDF report generated");
 * 
 * String formattedDate =
 * start.toLocalDate().format(DateTimeFormatter.ofPattern("d MMM yyyy")); String
 * subject = "Tower-wise RFID Report – " + location + " – " + formattedDate;
 * String body = "Dear Team,<br>" +
 * "Please find attached the RFID Punching Report for <strong>" + location +
 * "</strong>, covering Towers A to E for <strong>" + formattedDate +
 * "</strong>.<br>" +
 * "This report provides a detailed overview of employee swipe activity segmented by tower, including individual punch counts and total summaries.<br><br>"
 * + "<strong>Note:</strong><br>" +
 * "• <span style='color:red; font-weight:bold;'>Red</span> – Low punching activity<br>"
 * +
 * "• <span style='color:gray; font-weight:bold;'>Gray</span> – Normal or average punching activity<br><br>"
 * + "Thank you,<br><span style='font-size:13px;'>Chandru</span>";
 * 
 * emailService.sendWithAttachment(recipients.toArray(new String[0]), subject,
 * body, report); logger.info("📤 Scheduled email sent successfully for {}",
 * formattedDate);
 * 
 * } catch (Exception e) {
 * logger.error("❌ Error during scheduled email execution: {}", e.getMessage(),
 * e); if (testMode) throw new RuntimeException(e); }
 * logger.info("📅 [END] Scheduled email job completed"); } }
 */

/*
 * package com.example.emailscheduler;
 * 
 * 
 * import jakarta.annotation.PostConstruct; import org.slf4j.Logger; import
 * org.slf4j.LoggerFactory; import
 * org.springframework.beans.factory.annotation.Value; import
 * org.springframework.scheduling.annotation.Scheduled; import
 * org.springframework.stereotype.Component;
 * 
 * import com.example.dto.EmployeeFloorSummary; import
 * com.example.emailservice.Emailservices; import
 * com.example.pdfservice.PdfReportGenerator; import
 * com.example.repo.EmployeeSwipeRepository;
 * 
 * import java.time.LocalDateTime; import java.time.format.DateTimeFormatter;
 * import java.util.*; import java.util.concurrent.CopyOnWriteArrayList; import
 * java.util.stream.Stream;
 * 
 * @Component public class EmailScheduler {
 * 
 * private static final Logger logger =
 * LoggerFactory.getLogger(EmailScheduler.class);
 * 
 * private final Emailservices emailService; private final
 * EmployeeSwipeRepository repository; private final PdfReportGenerator
 * pdfReportGenerator;
 * 
 * private final List<String> recipients = new CopyOnWriteArrayList<>(); private
 * volatile boolean cronEnabled = true; private boolean testMode = false;
 * 
 * @Value("${send.recipient1}") private String r1;
 * 
 * @Value("${send.recipient2}") private String r2;
 * 
 * @Value("${send.recipient3}") private String r3;
 * 
 * public EmailScheduler(Emailservices emailService, EmployeeSwipeRepository
 * repository, PdfReportGenerator pdfReportGenerator) { this.emailService =
 * emailService; this.repository = repository; this.pdfReportGenerator =
 * pdfReportGenerator; }
 * 
 * @PostConstruct public void loadRecipients() { recipients.addAll(Stream.of(r1,
 * r2, r3) .filter(email -> email != null && !email.isBlank()) .toList());
 * logger.info("📧 Recipients loaded: {}", String.join(", ", recipients)); }
 * 
 * public void setTestMode(boolean testMode) { this.testMode = testMode; }
 * 
 * public void setCronEnabled(boolean enabled) { this.cronEnabled = enabled; }
 * 
 * public boolean isCronEnabled() { return cronEnabled; }
 * 
 * public List<String> getRecipients() { return recipients; }
 * 
 * public void addRecipient(String email) { if (email != null &&
 * email.matches(".+@.+\\..+")) { recipients.add(email);
 * logger.info("✅ Recipient added: {}", email); } else {
 * logger.warn("❌ Invalid recipient format: {}", email); } }
 * 
 * public void removeRecipient(String email) { recipients.remove(email);
 * logger.info("🗑️ Recipient removed: {}", email); }
 * 
 * @Scheduled(cron = "${send.email.expression}") public void sendPdfMail() { if
 * (!cronEnabled) { logger.info("⏸️ Cron job is disabled. Skipping execution.");
 * return; }
 * 
 * try {
 * 
 * 
 * LocalDateTime start = LocalDateTime.of(2025, 10, 13, 0, 0); LocalDateTime end
 * = start.plusDays(1); logger.debug("Report window: start={}, end={}", start,
 * end); String location = "MVL";
 * logger.debug("Report window: start={}, end={}", start, end);
 * 
 * 
 * 
 * LocalDateTime start =
 * LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
 * LocalDateTime end = start.plusDays(1); String location = "MVL";
 * 
 * List<Object[]> rawResults = repository.getTowerWiseSummaryBetween(start, end,
 * location); if (rawResults == null || rawResults.isEmpty()) {
 * logger.warn("⚠️ No swipe data found for {} between {} and {}", location,
 * start, end); return; }
 * 
 * Set<String> seenKeys = new HashSet<>(); List<EmployeeFloorSummary> summaries
 * = rawResults.stream() .map(row -> new EmployeeFloorSummary((Integer) row[0],
 * (String) row[1], (String) row[2], (String) row[3], ((Number)
 * row[4]).longValue(), ((Number) row[5]).longValue(), ((Number)
 * row[6]).longValue(), ((Number) row[7]).longValue(), ((Number)
 * row[8]).longValue())) .filter(s -> seenKeys.add(s.getEmployeeId() + "-" +
 * s.getDesignation())) .toList();
 * 
 * if (summaries.isEmpty()) {
 * logger.warn("⚠️ No deduplicated summaries to report for {}", location);
 * return; }
 * 
 * if (recipients.isEmpty()) {
 * logger.warn("⚠️ No recipients configured. Skipping email."); return; }
 * 
 * for (String email : recipients) { if (email == null ||
 * !email.matches(".+@.+\\..+")) { logger.error("❌ Invalid email address: {}",
 * email); if (testMode) throw new IllegalArgumentException("Invalid email: " +
 * email); return; } }
 * 
 * byte[] report = pdfReportGenerator.generateTowerSummaryPdf(summaries,
 * start.toLocalDate());
 * 
 * String formattedDate =
 * start.toLocalDate().format(DateTimeFormatter.ofPattern("d MMM yyyy")); String
 * body = "Dear Team,<br>" +
 * "Please find attached the RFID Punching Report for <strong>" + location +
 * "</strong>, covering Towers A to E for <strong>" + formattedDate +
 * "</strong>.<br>" +
 * "This report provides a detailed overview of employee swipe activity segmented by tower, including individual punch counts and total summaries.<br><br>"
 * + "<strong>Note:</strong><br>" +
 * "• <span style='color:red; font-weight:bold;'>Red</span> – Low punching activity<br>"
 * +
 * "• <span style='color:gray; font-weight:bold;'>Gray</span> – Normal or average punching activity<br><br>"
 * + "Thank you,<br>" + "<span style='font-size:13px;'>Chandru</span>";
 * 
 * 
 * 
 * emailService.sendWithAttachment(recipients.toArray(new String[0]), body,
 * body, report); logger.info("📤 Scheduled email sent successfully for {}",
 * formattedDate);
 * 
 * } catch (Exception e) {
 * logger.error("❌ Error during scheduled email execution: {}", e.getMessage(),
 * e); if (testMode) throw new RuntimeException(e); } } }
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */

/*
 * package com.example.emailscheduler; import
 * com.example.dto.EmployeeFloorSummary; import
 * com.example.emailservice.Emailservices; import
 * com.example.pdfservice.PdfReportGenerator; import
 * com.example.repo.EmployeeSwipeRepository; import org.slf4j.Logger; import
 * org.slf4j.LoggerFactory; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.scheduling.annotation.Scheduled; import
 * org.springframework.stereotype.Component;
 * 
 * import java.time.LocalDateTime; import java.time.format.DateTimeFormatter;
 * import java.util.*; import java.util.concurrent.CopyOnWriteArrayList;
 * 
 * @Component public class EmailScheduler {
 * 
 * private static final Logger logger =
 * LoggerFactory.getLogger(EmailScheduler.class);
 * 
 * @Autowired private Emailservices emailService;
 * 
 * @Autowired private EmployeeSwipeRepository repository;
 * 
 * @Autowired private PdfReportGenerator pdfReportGenerator;
 * 
 * private final List<String> recipients = new CopyOnWriteArrayList<>(); private
 * volatile boolean cronEnabled = true; private boolean testMode = false;
 * 
 * public void setTestMode(boolean testMode) { this.testMode = testMode; }
 * 
 * public void setCronEnabled(boolean enabled) { this.cronEnabled = enabled; }
 * 
 * public boolean isCronEnabled() { return cronEnabled; }
 * 
 * public List<String> getRecipients() { return recipients; }
 * 
 * public void addRecipient(String email) { recipients.add(email); }
 * 
 * public void removeRecipient(String email) { recipients.remove(email); }
 * 
 * @Scheduled(cron = "${send.email.expression}") public void sendPdfMail() { if
 * (!cronEnabled) { logger.info("⏸️ Cron job is disabled. Skipping execution.");
 * return; }
 * 
 * try { LocalDateTime start = LocalDateTime.of(2025, 10, 13, 0, 0);
 * LocalDateTime end = start.plusDays(1); String location = "MVL";
 * 
 * List<Object[]> rawResults = repository.getTowerWiseSummaryBetween(start, end,
 * location); if (rawResults == null || rawResults.isEmpty()) {
 * logger.warn("⚠️ No swipe data found for {} between {} and {}", location,
 * start, end); return; }
 * 
 * Set<String> seenKeys = new HashSet<>(); List<EmployeeFloorSummary> summaries
 * = rawResults.stream() .map(row -> new EmployeeFloorSummary((Integer) row[0],
 * (String) row[1], (String) row[2], (String) row[3], ((Number)
 * row[4]).longValue(), ((Number) row[5]).longValue(), ((Number)
 * row[6]).longValue(), ((Number) row[7]).longValue(), ((Number)
 * row[8]).longValue())) .filter(s -> seenKeys.add(s.getEmployeeId() + "-" +
 * s.getDesignation())) .toList();
 * 
 * if (summaries.isEmpty()) {
 * logger.warn("⚠️ No deduplicated summaries to report for {}", location);
 * return; }
 * 
 * if (recipients.isEmpty()) {
 * logger.warn("⚠️ No recipients configured. Skipping email."); return; }
 * 
 * for (String email : recipients) { if (email == null ||
 * !email.matches(".+@.+\\..+")) { logger.error("❌ Invalid email address: {}",
 * email); if (testMode) throw new IllegalArgumentException("Invalid email: " +
 * email); return; } }
 * 
 * byte[] report = pdfReportGenerator.generateTowerSummaryPdf(summaries,
 * start.toLocalDate());
 * 
 * String formattedDate =
 * start.toLocalDate().format(DateTimeFormatter.ofPattern("d MMM yyyy")); String
 * subject = "RFID Punching Report – " + location + " – " + formattedDate;
 * String body = "Dear Team,<br><br>" +
 * "Please find attached the RFID Punching Report for <strong>" + location +
 * "</strong> on <strong>" + formattedDate + "</strong>.<br><br>" +
 * "Thank you,<br><span style='font-size:13px;'>Chandru</span>";
 * 
 * emailService.sendWithAttachment(recipients.toArray(new String[0]), subject,
 * body, report); logger.info("📤 Scheduled email sent successfully for {}",
 * formattedDate);
 * 
 * } catch (Exception e) {
 * logger.error("❌ Error during scheduled email execution: {}", e.getMessage(),
 * e); } } }
 */