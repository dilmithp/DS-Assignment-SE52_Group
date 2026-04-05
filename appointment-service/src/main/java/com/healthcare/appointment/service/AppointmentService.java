package com.healthcare.appointment.service;

import com.healthcare.appointment.client.DoctorServiceClient;
import com.healthcare.appointment.client.PatientServiceClient;
import com.healthcare.appointment.dto.AppointmentRequest;
import com.healthcare.appointment.dto.AppointmentResponse;
import com.healthcare.appointment.entity.Appointment;
import com.healthcare.appointment.entity.AppointmentStatus;
import com.healthcare.appointment.exception.ResourceNotFoundException;
import com.healthcare.appointment.exception.DoubleBookingException;
import com.healthcare.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final DoctorServiceClient doctorClient;
    private final PatientServiceClient patientClient;

    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        // 1. Validation Rules: Validate Doctor & Patient existence
        // patientClient.getPatientById(request.getPatientId());
        // doctorClient.getDoctorById(request.getDoctorId());

        // 2. Prevent Double Booking
        boolean isDoubleBooked = repository.existsByDoctorIdAndAppointmentTimeAndStatusNot(
                request.getDoctorId(), 
                request.getAppointmentTime(), 
                AppointmentStatus.CANCELLED
        );
        if (isDoubleBooked) {
             throw new DoubleBookingException("Doctor is already booked at this time.");
        }

        // 3. Create Entity (Initially PENDING until Orchestrator handles payment/confirmation)
        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .appointmentTime(request.getAppointmentTime())
                .notes(request.getNotes())
                .status(AppointmentStatus.PENDING) 
                .build();

        return mapToResponse(repository.save(appointment));
    }

    public AppointmentResponse getAppointment(Long id) {
        return repository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
    }

    public List<AppointmentResponse> getPatientAppointments(Long patientId) {
        return repository.findByPatientId(patientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getDoctorAppointments(Long doctorId) {
        return repository.findByDoctorId(doctorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        appointment.setStatus(status);
        repository.save(appointment);
    }
    
    @Transactional
    public AppointmentResponse reschedule(Long id, AppointmentRequest req) {
        Appointment appointment = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getDoctorId().equals(req.getDoctorId()) || !appointment.getAppointmentTime().equals(req.getAppointmentTime())) {
            boolean isDoubleBooked = repository.existsByDoctorIdAndAppointmentTimeAndStatusNot(
                    req.getDoctorId(), 
                    req.getAppointmentTime(), 
                    AppointmentStatus.CANCELLED
            );
            if (isDoubleBooked) {
                 throw new DoubleBookingException("Doctor is already booked at this new time.");
            }
        }

        appointment.setDoctorId(req.getDoctorId());
        appointment.setAppointmentTime(req.getAppointmentTime());
        appointment.setNotes(req.getNotes());
        
        return mapToResponse(repository.save(appointment));
    }
    
    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .appointmentTime(appointment.getAppointmentTime())
                .notes(appointment.getNotes())
                .status(appointment.getStatus())
                .build();
    }
}
