package com.healthcare.telemedicine.controller;

import com.healthcare.telemedicine.dto.TelemedicineSessionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/telemedicine/session")
@Slf4j
public class TelemedicineController {

    @PostMapping("/create")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public TelemedicineSessionDto createSession() {
        // TODO: Integrate with Agora/Twilio Video API to create a room
        String sessionId = UUID.randomUUID().toString();
        log.info("Simulating creation of telemedicine session: {}", sessionId);
        return new TelemedicineSessionDto(sessionId, "https://meet.healthcare.platform/" + sessionId, "CREATED");
    }

    @GetMapping("/{sessionId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public TelemedicineSessionDto getSession(@PathVariable String sessionId) {
        // TODO: Integrate with Agora/Twilio Video API to fetch room details
        return new TelemedicineSessionDto(sessionId, "https://meet.healthcare.platform/" + sessionId, "ACTIVE");
    }

    @PutMapping("/{sessionId}/end")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public TelemedicineSessionDto endSession(@PathVariable String sessionId) {
        // TODO: Integrate with Agora/Twilio Video API to close the room
        log.info("Simulating ending of telemedicine session: {}", sessionId);
        return new TelemedicineSessionDto(sessionId, null, "ENDED");
    }
}
