package com.example.sendexceldatatoemail;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan(basePackages = "com.example.util")

@EnableJpaRepositories(basePackages = "com.example.repo") 
@ComponentScan(basePackages = "com.example")
@EnableScheduling
public class SendexceldatatoemailApplication {
    public static void main(String[] args) {
        SpringApplication.run(SendexceldatatoemailApplication.class, args);
    }
}