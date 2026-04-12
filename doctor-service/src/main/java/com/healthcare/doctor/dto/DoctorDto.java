package com.healthcare.doctor.dto;

public record DoctorDto(Long id, String name, String email, String phone, String specialty, String licenseNumber,boolean isVerified) {}
