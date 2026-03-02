package com.healthcare.orchestrator.controller;

import com.healthcare.orchestrator.service.SagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orchestrator")
@RequiredArgsConstructor
public class OrchestratorController {

    private final SagaService sagaService;

    @PostMapping("/start-booking")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> startBookingSaga(@RequestParam Long appointmentId,
                                                 @RequestParam Long patientId,
                                                 @RequestParam Long doctorId) {
        sagaService.startBookingSaga(appointmentId, patientId, doctorId);
        return ResponseEntity.accepted().build();
    }
}
