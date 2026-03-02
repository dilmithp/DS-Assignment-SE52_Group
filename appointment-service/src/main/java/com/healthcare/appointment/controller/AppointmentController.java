package com.healthcare.appointment.controller;

import com.healthcare.appointment.dto.AppointmentDto;
import com.healthcare.appointment.dto.AppointmentModifyRequest;
import com.healthcare.appointment.dto.AppointmentRequest;
import com.healthcare.appointment.service.AppointmentService;
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

    private final AppointmentService appointmentService;

    @PostMapping("/book")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PATIENT')")
    public AppointmentDto bookAppointment(@RequestBody @Valid AppointmentRequest request) {
        return appointmentService.bookAppointment(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public AppointmentDto getAppointment(@PathVariable Long id) {
        return appointmentService.getAppointment(id);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public AppointmentDto cancelAppointment(@PathVariable Long id) {
        return appointmentService.cancelAppointment(id);
    }

    @PutMapping("/{id}/modify")
    @PreAuthorize("hasRole('PATIENT')")
    public AppointmentDto modifyAppointment(@PathVariable Long id, @RequestBody @Valid AppointmentModifyRequest request) {
        return appointmentService.modifyAppointment(id, request);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public List<AppointmentDto> getAppointmentsByPatient(@PathVariable Long patientId) {
        return appointmentService.getAppointmentsByPatient(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<AppointmentDto> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        return appointmentService.getAppointmentsByDoctor(doctorId);
    }
}
