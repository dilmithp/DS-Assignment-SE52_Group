package com.healthcare.notification.dto;

import java.util.Map;

import lombok.Value;

@Value
public class NotificationEvent {
    String topic;
    Map<String, Object> payload;
}
