package com.healthcare.appointment.dto;

import com.healthcare.appointment.entity.AppointmentStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentResponse {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime appointmentTime;
    private String notes;
    private AppointmentStatus status;
}
