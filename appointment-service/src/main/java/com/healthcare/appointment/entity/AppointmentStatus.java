package com.healthcare.appointment.entity;

public enum AppointmentStatus {
    PENDING,    // Initial state before orchestration or payment succeeds
    CONFIRMED,  // Booking successful
    CANCELLED,  // Cancelled by patient or doctor
    FAILED,     // Booking orchestration/payment failed
    COMPLETED   // Appointment has taken place
}
