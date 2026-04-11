
package com.healthcare.doctor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PrescriptionRequest(
        @NotNull Long patientId,
        @NotNull Long appointmentId,
        @NotBlank String medication,
        @NotBlank String dosage,
        String notes
) {}