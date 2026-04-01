package com.healthcare.appointment.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "patient-service")
public interface PatientServiceClient {
}
