package com.healthcare.symptom.dto;
import java.util.List;
public record SymptomCheckResponse(List<String> possibleConditions, String recommendation) {}
