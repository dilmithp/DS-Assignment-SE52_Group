package com.healthcare.patient.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record PatientRegistrationRequest(
    @NotBlank String name, 
    @Email @NotBlank String email, 
    @NotBlank String phone, 
    LocalDate dateOfBirth, 
    String address
) {}
