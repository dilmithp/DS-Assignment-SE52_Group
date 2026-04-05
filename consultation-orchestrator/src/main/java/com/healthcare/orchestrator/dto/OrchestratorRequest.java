package com.healthcare.orchestrator.dto;

import lombok.Data;

@Data
public class OrchestratorRequest {
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
}
