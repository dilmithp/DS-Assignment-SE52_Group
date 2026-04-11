package com.healthcare.doctor.controller;

import com.healthcare.doctor.dto.AvailabilityDto;
import com.healthcare.doctor.dto.AvailabilityRequest;
import com.healthcare.doctor.dto.DoctorDto;
import com.healthcare.doctor.dto.DoctorRegistrationRequest;
import com.healthcare.doctor.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.healthcare.doctor.dto.PrescriptionDto;
import com.healthcare.doctor.dto.PrescriptionRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto register(@RequestBody @Valid DoctorRegistrationRequest request) {
        return doctorService.register(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public DoctorDto getDoctor(@PathVariable Long id) {
        return doctorService.getDoctor(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public DoctorDto updateDoctor(@PathVariable Long id, @RequestBody @Valid DoctorRegistrationRequest request) {
        return doctorService.updateDoctor(id, request);
    }

    @GetMapping("/specialty/{specialty}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public List<DoctorDto> getDoctorsBySpecialty(@PathVariable String specialty) {
        return doctorService.getDoctorsBySpecialty(specialty);
    }

    @PostMapping("/{id}/availability")
    @PreAuthorize("hasRole('DOCTOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public AvailabilityDto addAvailability(@PathVariable Long id, @RequestBody @Valid AvailabilityRequest request) {
        return doctorService.addAvailability(id, request);
    }

    @GetMapping("/{id}/availability")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public List<AvailabilityDto> getAvailability(@PathVariable Long id) {
        return doctorService.getAvailability(id);
    }

    @PutMapping("/{id}/appointments/{appointmentId}/accept")
    @PreAuthorize("hasRole('DOCTOR')")
    public Map<String, String> acceptAppointment(@PathVariable Long id, @PathVariable Long appointmentId) {
        return Map.of("status", "ACCEPTED", "appointmentId", appointmentId.toString(), "doctorId", id.toString());
    }

    @PutMapping("/{id}/appointments/{appointmentId}/reject")
    @PreAuthorize("hasRole('DOCTOR')")
    public Map<String, String> rejectAppointment(@PathVariable Long id, @PathVariable Long appointmentId) {
        return Map.of("status", "REJECTED", "appointmentId", appointmentId.toString(), "doctorId", id.toString());
    }

    @PostMapping("/{id}/prescriptions")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('DOCTOR')")
    public PrescriptionDto issuePrescription(@PathVariable Long id,
                                             @RequestBody @Valid PrescriptionRequest request) {
        return doctorService.issuePrescription(id, request);
    }

    @GetMapping("/prescriptions/patient/{patientId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public List<PrescriptionDto> getPrescriptionsByPatient(@PathVariable Long patientId) {
        return doctorService.getPrescriptionsByPatient(patientId);
    }
}
