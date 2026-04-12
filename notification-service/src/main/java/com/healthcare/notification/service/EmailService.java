package com.healthcare.notification.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    @Value("${sendgrid.from-name}")
    private String fromName;

    public void sendEmail(String toEmail, String subject, String htmlBody) {
        if (!StringUtils.hasText(toEmail)) {
            log.warn("Skipping email notification because recipient email is missing. Subject={}", subject);
            return;
        }

        try {
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(toEmail);
            Content content = new Content("text/html", htmlBody);
            Mail mail = new Mail(from, subject, to, content);

            SendGrid sendGrid = new SendGrid(apiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            log.info("Email sent to {} with subject '{}' via SendGrid. Status code={}",
                    toEmail, subject, response.getStatusCode());
        } catch (IOException e) {
            log.error("Failed to send email to {} with subject '{}': {}", toEmail, subject, e.getMessage(), e);
        }
    }

    public void sendAppointmentConfirmationToPatient(String patientEmail, String doctorName, String appointmentTime) {
        sendEmail(patientEmail,
                "Your appointment is confirmed",
                buildAppointmentConfirmationForPatientHtml(doctorName, appointmentTime));
    }

    public void sendNewAppointmentToDoctor(String doctorEmail, String patientName, String appointmentTime) {
        sendEmail(doctorEmail,
                "New appointment scheduled",
                buildNewAppointmentForDoctorHtml(patientName, appointmentTime));
    }

    public void sendPaymentReceiptToPatient(String patientEmail, Object amount, Object currency, Object appointmentId) {
        sendEmail(patientEmail,
                "Payment Receipt - Healthcare Platform",
                buildPaymentReceiptHtml(amount, currency, appointmentId));
    }

    public void sendConsultationFollowUpToPatient(String patientEmail, String doctorName) {
        sendEmail(patientEmail,
                "Your consultation summary",
                buildConsultationFollowUpHtml(doctorName));
    }

    public void sendAppointmentCancellationToPatient(String patientEmail, Object appointmentId) {
        sendEmail(patientEmail,
                "Appointment Cancelled",
                buildAppointmentCancellationHtml(appointmentId));
    }

    private String buildAppointmentConfirmationForPatientHtml(String doctorName, String appointmentTime) {
        return "<html><body>"
                + "<h3>Your appointment is confirmed</h3>"
                + "<p>Your appointment with Dr. " + safe(doctorName) + " at " + safe(appointmentTime)
                + " is confirmed.</p>"
                + "</body></html>";
    }

    private String buildNewAppointmentForDoctorHtml(String patientName, String appointmentTime) {
        return "<html><body>"
                + "<h3>New appointment scheduled</h3>"
                + "<p>You have a new appointment with patient " + safe(patientName)
                + " at " + safe(appointmentTime) + ".</p>"
                + "</body></html>";
    }

    private String buildPaymentReceiptHtml(Object amount, Object currency, Object appointmentId) {
        return "<html><body>"
                + "<h3>Payment Receipt - Healthcare Platform</h3>"
                + "<p>Payment of " + safe(amount) + " " + safe(currency)
                + " received for Appointment #" + safe(appointmentId) + ".</p>"
                + "</body></html>";
    }

    private String buildConsultationFollowUpHtml(String doctorName) {
        return "<html><body>"
                + "<h3>Your consultation summary</h3>"
                + "<p>Your consultation with Dr. " + safe(doctorName) + " has ended.</p>"
                + "<p>Check your patient portal for any prescriptions issued.</p>"
                + "</body></html>";
    }

    private String buildAppointmentCancellationHtml(Object appointmentId) {
        return "<html><body>"
                + "<h3>Appointment Cancelled</h3>"
                + "<p>Your Appointment #" + safe(appointmentId) + " has been cancelled.</p>"
                + "<p>If payment was made, a refund will be processed shortly.</p>"
                + "</body></html>";
    }

    private String safe(Object value) {
        return value == null ? "N/A" : String.valueOf(value);
    }
}
