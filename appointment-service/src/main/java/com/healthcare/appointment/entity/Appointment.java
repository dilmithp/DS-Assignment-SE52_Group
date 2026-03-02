package com.healthcare.appointment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;
    private Long doctorId;
    private LocalDateTime dateTime;
    
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
    
    @Enumerated(EnumType.STRING)
    private AppointmentType type;
}
