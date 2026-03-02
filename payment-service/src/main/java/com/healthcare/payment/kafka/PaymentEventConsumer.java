package com.healthcare.payment.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentEventConsumer {

    @KafkaListener(topics = "payment.initiated", groupId = "payment-group")
    public void consumePaymentInitiated(String message) {
        log.info("Received payment.initiated event: {}", message);
    }
}
