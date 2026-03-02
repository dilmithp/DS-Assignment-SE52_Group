package com.healthcare.symptom.controller;

import com.healthcare.symptom.dto.SymptomCheckRequest;
import com.healthcare.symptom.dto.SymptomCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/symptom-check")
@Slf4j
public class SymptomController {

    @PostMapping
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public SymptomCheckResponse checkSymptoms(@RequestBody SymptomCheckRequest request) {
        log.info("Received symptom check request for symptoms: {}", request.symptoms());
        
        // TODO: Integrate with OpenAI/Gemini API to get real predictions based on symptoms
        List<String> mockConditions = List.of("Common Cold", "Seasonal Allergies");
        String mockRecommendation = "Please rest and drink plenty of fluids. Consult a doctor if symptoms persist.";
        
        return new SymptomCheckResponse(mockConditions, mockRecommendation);
    }
}
