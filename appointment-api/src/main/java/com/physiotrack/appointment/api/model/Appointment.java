package com.physiotrack.appointment.api.model;

import java.time.LocalDateTime;

public class Appointment {
    private Long id;

    private Long patientId;
    private Long physioId;

    private LocalDateTime dateTime;
    private String details;

    private AppointmentRequestType requestType;
    private AppointmentStatus status;

    private Long targetAppointmentId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getPhysioId() { return physioId; }
    public void setPhysioId(Long physioId) { this.physioId = physioId; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public AppointmentRequestType getRequestType() { return requestType; }
    public void setRequestType(AppointmentRequestType requestType) { this.requestType = requestType; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public Long getTargetAppointmentId() { return targetAppointmentId; }
    public void setTargetAppointmentId(Long targetAppointmentId) { this.targetAppointmentId = targetAppointmentId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
