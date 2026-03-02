package com.healthcare.telemedicine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.healthcare.telemedicine", "com.healthcare.security"})
public class TelemedicineServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelemedicineServiceApplication.class, args);
    }
}
