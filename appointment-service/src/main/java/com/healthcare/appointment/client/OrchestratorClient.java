package com.healthcare.appointment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "consultation-orchestrator")
public interface OrchestratorClient {
    @PostMapping("/api/orchestrator/start-booking")
    void startBookingSaga(@RequestParam("appointmentId") Long appointmentId, 
                          @RequestParam("patientId") Long patientId, 
                          @RequestParam("doctorId") Long doctorId);
}
