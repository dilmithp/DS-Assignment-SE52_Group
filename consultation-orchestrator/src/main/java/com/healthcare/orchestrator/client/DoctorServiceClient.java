package com.healthcare.orchestrator.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "doctor-service")
public interface DoctorServiceClient {
}
