package com.healthcare.patient.service;

import com.healthcare.patient.client.DoctorClient;
import com.healthcare.patient.dto.PatientDto;
import com.healthcare.patient.dto.PatientRegistrationRequest;
import com.healthcare.patient.entity.Patient;
import com.healthcare.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.healthcare.patient.dto.MedicalReportDto;
import com.healthcare.patient.dto.ReportMetadataRequest;
import com.healthcare.patient.entity.MedicalReport;
import com.healthcare.patient.repository.MedicalReportRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final MedicalReportRepository medicalReportRepository;
    private final DoctorClient doctorClient;

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

    public MedicalReportDto uploadReport(Long patientId, ReportMetadataRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        MedicalReport report = MedicalReport.builder()
                .patientId(patient.getId())
                .reportName(request.reportName())
                .description(request.description())
                .fileUrl(request.fileUrl())
                .build();

        report = medicalReportRepository.save(report);
        return mapToMedicalReportDto(report);
    }

    public List<MedicalReportDto> getReports(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return medicalReportRepository.findByPatientId(patientId).stream()
                .map(this::mapToMedicalReportDto)
                .collect(Collectors.toList());
    }

    public Object getPrescriptions(Long patientId) {
        // 1. Check if patient exists
        patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // 2. Actually call the doctor-service using VIP token!
        return doctorClient.getPrescriptionsByPatient(patientId);
    }

    public Map<String, Object> getPatientHistory(Long patientId) {
        // 1. Check if patient exists
        patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // 2. Gather the Data
        List<MedicalReportDto> reports = getReports(patientId); // From your native database
        Object prescriptions = getPrescriptions(patientId);     // From the doctor-service

        // 3. Bundle it together
        Map<String, Object> fullHistory = new HashMap<>();
        fullHistory.put("reports", reports);
        fullHistory.put("prescriptions", prescriptions);

        return fullHistory;
    }

    private MedicalReportDto mapToMedicalReportDto(MedicalReport report) {
        return new MedicalReportDto(report.getId(), report.getPatientId(), report.getReportName(),
                report.getDescription(), report.getFileUrl(), report.getUploadedAt());
    }
}
