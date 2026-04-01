package com.healthcare.appointment.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "doctor-service")
public interface DoctorServiceClient {
}
