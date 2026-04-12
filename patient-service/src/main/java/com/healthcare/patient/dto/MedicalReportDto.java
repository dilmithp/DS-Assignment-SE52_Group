package com.healthcare.patient.dto;

import java.time.LocalDateTime;

public record MedicalReportDto(Long id, Long patientId, String reportName,
                               String description, String fileUrl,
                               LocalDateTime uploadedAt) {}