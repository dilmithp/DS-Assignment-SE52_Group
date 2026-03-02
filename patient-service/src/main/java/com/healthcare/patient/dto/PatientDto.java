package com.healthcare.patient.dto;

import java.time.LocalDate;

public record PatientDto(Long id, String name, String email, String phone, LocalDate dateOfBirth, String address) {}
