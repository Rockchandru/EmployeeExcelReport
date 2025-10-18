package com.example.sendexceldatatoemail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
    "com.example.reportjobrepository",
    "com.example.repo"
})
@EntityScan(basePackages = {
    "com.example.reportjobutil",
    "com.example.util"
})
@ComponentScan(basePackages = "com.example")
@EnableScheduling
public class SendexceldatatoemailApplication {

    private static final Logger logger = LoggerFactory.getLogger(SendexceldatatoemailApplication.class);

    public static void main(String[] args) {
        logger.info("Starting SendexceldatatoemailApplication...");
        SpringApplication.run(SendexceldatatoemailApplication.class, args);
        logger.info("SendexceldatatoemailApplication started successfully.");
    }
}
