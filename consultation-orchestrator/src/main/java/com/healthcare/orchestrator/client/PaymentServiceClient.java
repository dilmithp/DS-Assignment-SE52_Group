package com.healthcare.orchestrator.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {
    @PostMapping("/api/payments/initiate")
    Map<String, String> initiatePayment(@RequestBody Map<String, Object> request);

    @PostMapping("/api/payments/{id}/refund")
    Map<String, String> refundPayment(@PathVariable("id") Long id);
}
