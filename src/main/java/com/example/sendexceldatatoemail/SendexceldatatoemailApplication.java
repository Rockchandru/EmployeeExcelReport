package com.example.sendexceldatatoemail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.sendexceldatatoemail",
    "com.example.emailcontroller",
    "com.example.emailservice",
    "com.example.emailscheduler",
    "com.example.backup",
    "com.example.pdfservice",
    "com.example.repo",
    "com.example.dto",
    "com.example.util" // ✅ added to scan EmployeeSwipe
})
@EnableJpaRepositories(basePackages = {
    "com.example.repo"
})
@EntityScan(basePackages = {
    "com.example.dto",
    "com.example.util" // ✅ added to register EmployeeSwipe as a JPA entity
})
@EnableScheduling
public class SendexceldatatoemailApplication {
    private static final Logger logger = LoggerFactory.getLogger(SendexceldatatoemailApplication.class);

    public static void main(String[] args) {
        logger.info("🚀 Starting SendexceldatatoemailApplication...");
        SpringApplication.run(SendexceldatatoemailApplication.class, args);
        logger.info("✅ SendexceldatatoemailApplication started successfully.");
    }
}
