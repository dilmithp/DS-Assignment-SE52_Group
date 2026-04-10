package com.healthcare.appointment.dto;

import com.healthcare.appointment.entity.AppointmentType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AppointmentModifyRequest(
    @NotNull LocalDateTime dateTime,
    @NotNull AppointmentType type
) {}
