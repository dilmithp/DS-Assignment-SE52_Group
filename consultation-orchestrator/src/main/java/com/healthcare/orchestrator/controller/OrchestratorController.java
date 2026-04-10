package com.healthcare.orchestrator.controller;

import com.healthcare.orchestrator.service.SagaService;
import com.healthcare.orchestrator.dto.OrchestratorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orchestrator")
@RequiredArgsConstructor
public class OrchestratorController {

    private final SagaService sagaService;

    @PostMapping("/start-booking")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> startBookingSaga(@RequestBody OrchestratorRequest request) {
        sagaService.startBookingSaga(request.getAppointmentId(), request.getPatientId(), request.getDoctorId());
        return ResponseEntity.accepted().build();
    }
}
