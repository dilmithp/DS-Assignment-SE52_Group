package com.healthcare.doctor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DoctorRegistrationRequest(
    @NotBlank String name,
    @Email @NotBlank String email,
    @NotBlank String phone,
    @NotBlank String specialty,
    @NotBlank String licenseNumber
) {}
