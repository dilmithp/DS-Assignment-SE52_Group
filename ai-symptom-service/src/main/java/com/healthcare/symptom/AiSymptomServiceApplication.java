package com.healthcare.symptom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.healthcare.symptom", "com.healthcare.security"})
public class AiSymptomServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiSymptomServiceApplication.class, args);
    }
}
