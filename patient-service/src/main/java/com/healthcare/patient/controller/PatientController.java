package com.healthcare.patient.controller;

import com.healthcare.patient.dto.PatientDto;
import com.healthcare.patient.dto.PatientRegistrationRequest;
import com.healthcare.patient.dto.ReportMetadataRequest;
import com.healthcare.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto register(@RequestBody @Valid PatientRegistrationRequest request) {
        return patientService.register(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public PatientDto getPatient(@PathVariable Long id) {
        return patientService.getPatient(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    public PatientDto updatePatient(@PathVariable Long id, @RequestBody @Valid PatientRegistrationRequest request) {
        return patientService.updatePatient(id, request);
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public List<String> getPatientHistory(@PathVariable Long id) {
        // Stub for retrieving patient history
        patientService.getPatient(id); // Ensure patient exists
        return List.of("Medical History Record 1", "Medical History Record 2");
    }

    @PostMapping("/{id}/reports")
    @PreAuthorize("hasRole('PATIENT')")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> uploadReport(@PathVariable Long id, @RequestBody ReportMetadataRequest request) {
        // Stub for uploading medical report
        patientService.getPatient(id); // Ensure patient exists
        return Map.of("status", "Report metadata saved", "reportName", request.reportName());
    }
}
