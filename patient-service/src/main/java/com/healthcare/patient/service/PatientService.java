package com.healthcare.patient.service;

import com.healthcare.patient.dto.PatientDto;
import com.healthcare.patient.dto.PatientRegistrationRequest;
import com.healthcare.patient.entity.Patient;
import com.healthcare.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientDto register(PatientRegistrationRequest request) {
        Patient patient = Patient.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .dateOfBirth(request.dateOfBirth())
                .address(request.address())
                .build();
        patient = patientRepository.save(patient);
        return mapToDto(patient);
    }

    public PatientDto getPatient(Long id) {
        return patientRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    public PatientDto updatePatient(Long id, PatientRegistrationRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        patient.setName(request.name());
        patient.setEmail(request.email());
        patient.setPhone(request.phone());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setAddress(request.address());
        
        patient = patientRepository.save(patient);
        return mapToDto(patient);
    }

    private PatientDto mapToDto(Patient patient) {
        return new PatientDto(patient.getId(), patient.getName(), patient.getEmail(),
                patient.getPhone(), patient.getDateOfBirth(), patient.getAddress());
    }
}
