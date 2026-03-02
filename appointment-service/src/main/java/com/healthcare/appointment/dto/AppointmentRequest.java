package com.healthcare.appointment.dto;

import com.healthcare.appointment.entity.AppointmentType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AppointmentRequest(
    @NotNull Long patientId,
    @NotNull Long doctorId,
    @NotNull LocalDateTime dateTime,
    @NotNull AppointmentType type
) {}
