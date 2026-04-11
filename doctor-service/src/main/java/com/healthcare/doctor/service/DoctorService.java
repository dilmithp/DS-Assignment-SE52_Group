package com.healthcare.doctor.service;

import com.healthcare.doctor.dto.AvailabilityDto;
import com.healthcare.doctor.dto.AvailabilityRequest;
import com.healthcare.doctor.dto.DoctorDto;
import com.healthcare.doctor.dto.DoctorRegistrationRequest;
import com.healthcare.doctor.entity.Availability;
import com.healthcare.doctor.entity.Doctor;
import com.healthcare.doctor.repository.AvailabilityRepository;
import com.healthcare.doctor.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.healthcare.doctor.dto.PrescriptionDto;
import com.healthcare.doctor.dto.PrescriptionRequest;
import com.healthcare.doctor.entity.Prescription;
import com.healthcare.doctor.repository.PrescriptionRepository;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AvailabilityRepository availabilityRepository;
    private final PrescriptionRepository prescriptionRepository;

    public DoctorDto register(DoctorRegistrationRequest request) {
        Doctor doctor = Doctor.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .specialty(request.specialty())
                .licenseNumber(request.licenseNumber())
                .build();
        doctor = doctorRepository.save(doctor);
        return mapToDto(doctor);
    }

    public DoctorDto getDoctor(Long id) {
        return doctorRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public List<DoctorDto> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialtyIgnoreCase(specialty).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public DoctorDto updateDoctor(Long id, DoctorRegistrationRequest request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setName(request.name());
        doctor.setEmail(request.email());
        doctor.setPhone(request.phone());
        doctor.setSpecialty(request.specialty());
        doctor.setLicenseNumber(request.licenseNumber());

        doctor = doctorRepository.save(doctor);
        return mapToDto(doctor);
    }

    @Transactional
    public AvailabilityDto addAvailability(Long doctorId, AvailabilityRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Availability availability = Availability.builder()
                .doctor(doctor)
                .date(request.date())
                .timeSlots(request.timeSlots())
                .build();

        availability = availabilityRepository.save(availability);
        return mapToDto(availability);
    }

    public List<AvailabilityDto> getAvailability(Long doctorId) {
        return availabilityRepository.findByDoctorId(doctorId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private DoctorDto mapToDto(Doctor doctor) {
        return new DoctorDto(doctor.getId(), doctor.getName(), doctor.getEmail(),
                doctor.getPhone(), doctor.getSpecialty(), doctor.getLicenseNumber());
    }

    private AvailabilityDto mapToDto(Availability availability) {
        return new AvailabilityDto(availability.getId(), availability.getDoctor().getId(),
                availability.getDate(), availability.getTimeSlots());
    }

    @Transactional
    public PrescriptionDto issuePrescription(Long doctorId, PrescriptionRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Prescription prescription = Prescription.builder()
                .doctorId(doctor.getId())
                .patientId(request.patientId())
                .appointmentId(request.appointmentId())
                .medication(request.medication())
                .dosage(request.dosage())
                .notes(request.notes())
                .build();

        prescription = prescriptionRepository.save(prescription);
        return mapToPrescriptionDto(prescription);
    }

    public List<PrescriptionDto> getPrescriptionsByPatient(Long patientId) {
        // 1. Find all prescription entities for this patient
        return prescriptionRepository.findByPatientId(patientId)
                .stream()
                // 2. Map each Entity to a DTO (to send back as JSON)
                .map(this::mapToPrescriptionDto)
                .collect(Collectors.toList());
    }

    private PrescriptionDto mapToPrescriptionDto(Prescription prescription) {
        return new PrescriptionDto(
                prescription.getId(),
                prescription.getDoctorId(),
                prescription.getPatientId(),
                prescription.getAppointmentId(),
                prescription.getMedication(),
                prescription.getDosage(),
                prescription.getNotes(),
                prescription.getIssuedAt()
        );
    }
}
