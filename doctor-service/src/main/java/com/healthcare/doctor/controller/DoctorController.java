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

    @PutMapping("/{id}/appointments/{apptId}/accept")
    @PreAuthorize("hasRole('DOCTOR')")
    public Map<String, String> acceptAppointment(@PathVariable Long id, @PathVariable Long apptId) {
        return Map.of("status", "ACCEPTED", "appointmentId", apptId.toString(), "doctorId", id.toString());
    }

    @PutMapping("/{id}/appointments/{apptId}/reject")
    @PreAuthorize("hasRole('DOCTOR')")
    public Map<String, String> rejectAppointment(@PathVariable Long id, @PathVariable Long apptId) {
        return Map.of("status", "REJECTED", "appointmentId", apptId.toString(), "doctorId", id.toString());
    }
}
