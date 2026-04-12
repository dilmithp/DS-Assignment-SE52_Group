
package com.healthcare.doctor.dto;

import java.time.LocalDateTime;

public record PrescriptionDto(
        Long id,
        Long doctorId,
        Long patientId,
        Long appointmentId,
        String medication,
        String dosage,
        String notes,
        LocalDateTime issuedAt
) {}