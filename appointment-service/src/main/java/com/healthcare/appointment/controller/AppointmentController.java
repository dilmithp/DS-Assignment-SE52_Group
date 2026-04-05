package com.healthcare.appointment.controller;

import com.healthcare.appointment.dto.AppointmentRequest;
import com.healthcare.appointment.dto.AppointmentResponse;
import com.healthcare.appointment.entity.AppointmentStatus;
import com.healthcare.appointment.service.AppointmentService;
import com.healthcare.appointment.client.DoctorServiceClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;
    private final DoctorServiceClient doctorClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public AppointmentResponse createAppointment(@Valid @RequestBody AppointmentRequest request) {
        return service.createAppointment(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public AppointmentResponse getAppointment(@PathVariable Long id) {
        return service.getAppointment(id);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PATIENT') and #patientId == authentication.principal.id)")
    public List<AppointmentResponse> getAppointmentsByPatient(@PathVariable Long patientId) {
        return service.getPatientAppointments(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public List<AppointmentResponse> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        return service.getDoctorAppointments(doctorId);
    }

    @PutMapping("/{id}")
    public AppointmentResponse rescheduleAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentRequest req) {
        return service.reschedule(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelAppointment(@PathVariable Long id) {
        service.updateStatus(id, AppointmentStatus.CANCELLED);
    }

    @GetMapping("/status/{id}")
    public AppointmentStatus trackStatus(@PathVariable Long id) {
        return service.getAppointment(id).getStatus();
    }

    @PatchMapping("/{id}/status")
    public void patchStatus(@PathVariable Long id, @RequestParam AppointmentStatus status) {
        service.updateStatus(id, status);
    }

    @GetMapping("/doctors/search")
    public Object searchDoctors(@RequestParam String specialty) {
        return doctorClient.searchDoctorsBySpecialty(specialty);
    }
}
