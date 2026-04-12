package com.healthcare.doctor.client;

import com.healthcare.security.FeignJwtInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service", configuration = FeignJwtInterceptor.class)
public interface PatientClient {

    // This calls the history endpoint we fixed earlier in the patient-service!
    @GetMapping("/api/patients/{id}/history")
    Object getPatientHistory(@PathVariable("id") Long id);
}