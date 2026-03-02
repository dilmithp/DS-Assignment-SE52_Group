package com.healthcare.doctor.dto;

import java.time.LocalDate;
import java.util.List;

public record AvailabilityDto(Long id, Long doctorId, LocalDate date, List<String> timeSlots) {}
