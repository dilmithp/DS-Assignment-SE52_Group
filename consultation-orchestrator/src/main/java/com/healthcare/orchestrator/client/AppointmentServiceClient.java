package com.healthcare.orchestrator.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "appointment-service", path = "/api/appointments")
public interface AppointmentServiceClient {
    
    @PatchMapping("/{id}/status")
    void updateAppointmentStatus(@PathVariable("id") Long id, @RequestParam("status") String status);
}
