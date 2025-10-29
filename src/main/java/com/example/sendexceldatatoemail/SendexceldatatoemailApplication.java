package com.example.sendexceldatatoemail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    "com.example.util",
    "com.example.reportjobservice",      // ✅ added
    "com.example.reportjobscheduler"     // ✅ added
})
@EnableJpaRepositories(basePackages = {
    "com.example.repo",
    "com.example.reportjobrepository"
})
@EntityScan(basePackages = {
    "com.example.dto",
    "com.example.util",
    "com.example.reportjobutil"
})
@EnableScheduling
public class SendexceldatatoemailApplication {
    private static final Logger logger = LoggerFactory.getLogger(SendexceldatatoemailApplication.class);

    public static void main(String[] args) {
        logger.info("🚀 Starting SendexceldatatoemailApplication...");
        SpringApplication.run(SendexceldatatoemailApplication.class, args);
        logger.info("✅ SendexceldatatoemailApplication started successfully.");
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
