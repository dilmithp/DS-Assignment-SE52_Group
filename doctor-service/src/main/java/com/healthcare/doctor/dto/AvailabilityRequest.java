package com.healthcare.doctor.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record AvailabilityRequest(
    @NotNull LocalDate date,
    @NotNull List<String> timeSlots
) {}
