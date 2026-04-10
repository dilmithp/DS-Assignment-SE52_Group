package com.healthcare.appointment.service;

import com.healthcare.appointment.client.DoctorServiceClient;
import com.healthcare.appointment.client.OrchestratorClient;
import com.healthcare.appointment.client.PatientServiceClient;
import com.healthcare.appointment.dto.AppointmentDto;
import com.healthcare.appointment.dto.AppointmentModifyRequest;
import com.healthcare.appointment.dto.AppointmentRequest;
import com.healthcare.appointment.entity.Appointment;
import com.healthcare.appointment.entity.AppointmentStatus;
import com.healthcare.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorServiceClient doctorServiceClient;
    private final PatientServiceClient patientServiceClient;
    private final OrchestratorClient orchestratorClient;

    @Transactional
    public AppointmentDto bookAppointment(AppointmentRequest request) {
        // Validate patient and doctor exist by querying other services
        patientServiceClient.getPatient(request.patientId());
        doctorServiceClient.getDoctor(request.doctorId());

        Appointment appointment = Appointment.builder()
                .patientId(request.patientId())
                .doctorId(request.doctorId())
                .dateTime(request.dateTime())
                .type(request.type())
                .status(AppointmentStatus.PENDING)
                .build();

        appointment = appointmentRepository.save(appointment);

        // Trigger Orchestrator Saga
        orchestratorClient.startBookingSaga(appointment.getId(), appointment.getPatientId(), appointment.getDoctorId());

        return mapToDto(appointment);
    }

    public AppointmentDto getAppointment(Long id) {
        return appointmentRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    @Transactional
    public AppointmentDto cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment = appointmentRepository.save(appointment);
        return mapToDto(appointment);
    }

    @Transactional
    public AppointmentDto modifyAppointment(Long id, AppointmentModifyRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setDateTime(request.dateTime());
        appointment.setType(request.type());
        appointment = appointmentRepository.save(appointment);
        return mapToDto(appointment);
    }

    public List<AppointmentDto> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<AppointmentDto> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private AppointmentDto mapToDto(Appointment appointment) {
        return new AppointmentDto(appointment.getId(), appointment.getPatientId(),
                appointment.getDoctorId(), appointment.getDateTime(),
                appointment.getStatus(), appointment.getType());
    }
}
