package com.healthcare.orchestrator.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "patient-service")
public interface PatientServiceClient {
}
