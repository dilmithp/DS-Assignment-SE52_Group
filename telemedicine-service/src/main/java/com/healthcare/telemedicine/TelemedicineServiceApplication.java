package com.healthcare.telemedicine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.healthcare.telemedicine", "com.healthcare.security"})
public class TelemedicineServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelemedicineServiceApplication.class, args);
    }
}
