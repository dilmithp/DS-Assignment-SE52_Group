package com.healthcare.orchestrator.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "appointment-service")
public interface AppointmentServiceClient {
}
