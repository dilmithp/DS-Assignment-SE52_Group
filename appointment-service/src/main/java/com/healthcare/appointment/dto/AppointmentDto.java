package com.healthcare.appointment.dto;

import com.healthcare.appointment.entity.AppointmentStatus;
import com.healthcare.appointment.entity.AppointmentType;
import java.time.LocalDateTime;

public record AppointmentDto(Long id, Long patientId, Long doctorId, LocalDateTime dateTime, AppointmentStatus status, AppointmentType type) {}
