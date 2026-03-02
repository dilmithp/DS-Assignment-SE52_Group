package com.healthcare.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = {"com.healthcare.orchestrator", "com.healthcare.security"})
public class ConsultationOrchestratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsultationOrchestratorApplication.class, args);
    }
}
