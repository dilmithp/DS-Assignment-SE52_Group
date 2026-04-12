package com.healthcare.payment.kafka;

import com.healthcare.payment.dto.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private static final String TOPIC = "payment.completed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCompletedEvent(PaymentCompletedEvent event) {
        try {
            kafkaTemplate.send(TOPIC, event.getPaymentId().toString(), event)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            log.error("Failed to publish payment completed event for paymentId={}", event.getPaymentId(), throwable);
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to publish payment completed event for paymentId={}", event.getPaymentId(), e);
        }
    }
}