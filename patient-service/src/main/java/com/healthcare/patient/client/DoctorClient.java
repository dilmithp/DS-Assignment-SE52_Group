package com.healthcare.patient.client;

import com.healthcare.patient.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "doctor-service", configuration = FeignClientConfig.class)
public interface DoctorClient {

    @GetMapping("/api/doctors/prescriptions/patient/{patientId}")
    Object getPrescriptionsByPatient(@PathVariable("patientId") Long patientId);
}