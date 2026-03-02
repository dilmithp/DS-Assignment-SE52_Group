package com.healthcare.orchestrator.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "doctor-service")
public interface DoctorServiceClient {
    @PutMapping("/api/doctors/{id}/appointments/{apptId}/accept")
    Map<String, String> acceptAppointment(@PathVariable("id") Long id, @PathVariable("apptId") Long apptId);
}
