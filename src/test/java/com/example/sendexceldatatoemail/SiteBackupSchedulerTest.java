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