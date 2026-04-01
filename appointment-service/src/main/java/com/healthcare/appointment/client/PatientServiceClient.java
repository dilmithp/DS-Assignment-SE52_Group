package com.healthcare.appointment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service")
public interface PatientServiceClient {
    @GetMapping("/api/patients/{id}")
    Object getPatient(@PathVariable("id") Long id);
}
