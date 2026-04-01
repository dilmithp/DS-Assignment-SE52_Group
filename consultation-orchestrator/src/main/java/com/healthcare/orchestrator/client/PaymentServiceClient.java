package com.healthcare.orchestrator.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {
    @PostMapping("/api/payments")
    Map<String, String> initiatePayment(@RequestBody Map<String, Object> request);

    @PostMapping("/api/payments/{id}/refund")
    void refundPayment(@PathVariable("id") Long id);
}
