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
import com.healthcare.patient.dto.MedicalReportDto;


import java.util.List;

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
    public Object getPatientHistory(@PathVariable Long id) {
        // We changed the return type to Object, and call the new service method
        return patientService.getPatientHistory(id);
    }

    @PostMapping("/{id}/reports")
    @PreAuthorize("hasRole('PATIENT')")
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalReportDto uploadReport(@PathVariable Long id, @RequestBody ReportMetadataRequest request) {
        return patientService.uploadReport(id, request);
    }

    @GetMapping("/{id}/reports")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public List<MedicalReportDto> getReports(@PathVariable Long id) {
        return patientService.getReports(id);
    }

    @GetMapping("/{id}/prescriptions")
    @PreAuthorize("hasRole('PATIENT')")
    public Object getPrescriptions(@PathVariable Long id) {
        return patientService.getPrescriptions(id);
    }
}
