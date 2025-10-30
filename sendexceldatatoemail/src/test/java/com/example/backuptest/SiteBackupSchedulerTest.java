package com.example.backuptest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import com.example.backup.SiteBackupScheduler;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SiteBackupSchedulerTest {

    private SiteBackupScheduler scheduler;

    @TempDir Path tempSourceDir;
    @TempDir Path tempBackupDir;
    @TempDir Path tempRemoteDir;

    @BeforeEach
    void setup() throws Exception {
        scheduler = new SiteBackupScheduler();

        // Inject test paths
        setPrivateField(scheduler, "sourceDir", tempSourceDir.toString());
        setPrivateField(scheduler, "backupDir", tempBackupDir.toString());
        setPrivateField(scheduler, "remoteDir", tempRemoteDir.toString());
        scheduler.setTestMode(true);

        // Create a sample file in sourceDir
        Files.writeString(tempSourceDir.resolve("sample.txt"), "This is a test file.");
    }

    @Test
    void testTriggerBackupManually_shouldCreateZipAndCopyToRemote() {
        String result = scheduler.triggerBackupManually();

        assertTrue(result.startsWith("✅ Backup triggered successfully"));
        assertNotEquals("Never", scheduler.getLastBackupTime());

        // Verify backup ZIP exists in remoteDir
        List<Path> remoteFiles = listFiles(tempRemoteDir);
        assertEquals(1, remoteFiles.size());
        assertTrue(remoteFiles.get(0).getFileName().toString().endsWith(".zip"));
    }

    @Test
    void testTriggerBackupManually_missingSource_shouldStillSucceed() throws Exception {
        Path missingSource = tempSourceDir.resolve("missing");
        setPrivateField(scheduler, "sourceDir", missingSource.toString());

        String result = scheduler.triggerBackupManually();
        assertTrue(result.startsWith("✅ Backup triggered successfully"));
    }

    @Test
    void testScheduledBackup_shouldRunWithoutException() {
        assertDoesNotThrow(() -> scheduler.scheduledBackup());
    }

    @Test
    void testLastBackupTime_shouldUpdateAfterBackup() {
        assertEquals("Never", scheduler.getLastBackupTime());
        scheduler.triggerBackupManually();
        assertNotEquals("Never", scheduler.getLastBackupTime());
    }

    // Utility to inject private fields
    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // Utility to list files in a directory
    private List<Path> listFiles(Path dir) {
        try (var stream = Files.list(dir)) {
            return stream.filter(Files::isRegularFile).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}














/*package com.example.sendexceldatatoemail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backup.SiteBackupScheduler;

import static org.junit.jupiter.api.Assertions.*;

public class SiteBackupSchedulerTest {

    private SiteBackupScheduler scheduler;

    @BeforeEach
    void setup() {
        scheduler = new SiteBackupScheduler();
        scheduler.setTestMode(true);
    }

    @Test
    void testTriggerBackupManually_success() {
        String result = scheduler.triggerBackupManually();
        assertTrue(result.startsWith("✅ Backup triggered successfully"));
    }

    @Test
    void testGetLastBackupTime_afterTrigger() {
        scheduler.triggerBackupManually();
        String time = scheduler.getLastBackupTime();
        assertNotEquals("Never", time);
    }
}
*/