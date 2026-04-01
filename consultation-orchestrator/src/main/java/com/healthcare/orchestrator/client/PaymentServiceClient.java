package com.healthcare.orchestrator.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {
}
