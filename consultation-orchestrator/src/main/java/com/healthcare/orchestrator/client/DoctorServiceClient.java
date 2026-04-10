package com.healthcare.orchestrator.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "doctor-service")
public interface DoctorServiceClient {
    @PostMapping("/api/doctors/{doctorId}/appointments/{appointmentId}/accept")
    void acceptAppointment(@PathVariable("doctorId") Long doctorId, @PathVariable("appointmentId") Long appointmentId);
}
