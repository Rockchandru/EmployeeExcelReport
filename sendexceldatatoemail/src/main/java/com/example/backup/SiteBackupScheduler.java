package com.example.backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class SiteBackupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SiteBackupScheduler.class);

    // ✅ Update these paths to match your Windows environment
    private  String sourceDir = "C:\\file backups";
    private  String backupDir = "C:\\temp";
    private  String remoteDir = "G:\\automatic backupdric";

    private boolean testMode = false;
    private String lastBackupTime = "Never";

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public String getLastBackupTime() {
        return lastBackupTime;
    }

    public String triggerBackupManually() {
        try {
            performBackup();
            return "✅ Backup triggered successfully at " + lastBackupTime;
        } catch (Exception e) {
            logger.error("❌ Manual backup failed", e);
            return "❌ Manual backup failed: " + e.getMessage();
        }
    }

    @Scheduled(cron = "${site.backup.cron}")
    public void scheduledBackup() {
        try {
            performBackup();
        } catch (Exception e) {
            logger.error("❌ Scheduled backup failed", e);
            if (testMode) throw new RuntimeException("Scheduled backup failed", e);
        }
    }

    private void performBackup() throws IOException {
        logger.info("🔄 Starting site backup...");

        // Ensure directories exist
        Files.createDirectories(Paths.get(sourceDir));
        Files.createDirectories(Paths.get(backupDir));
        Files.createDirectories(Paths.get(remoteDir));

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String zipFileName = "site-backup-" + timestamp + ".zip";
        Path zipPath = Paths.get(backupDir, zipFileName);

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Path sourcePath = Paths.get(sourceDir);
            Files.walk(sourcePath)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        Path relativePath = sourcePath.relativize(file);
                        zos.putNextEntry(new ZipEntry(relativePath.toString()));
                        Files.copy(file, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        logger.error("❌ Failed to add file to zip: {}", file, e);
                    }
                });
        }

        Path remotePath = Paths.get(remoteDir, zipFileName);
        Files.copy(zipPath, remotePath, StandardCopyOption.REPLACE_EXISTING);
        lastBackupTime = new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(new Date());

        logger.info("✅ Backup completed and stored at {}", remotePath);
    }
}
