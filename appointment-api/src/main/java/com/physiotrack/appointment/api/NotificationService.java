package com.physiotrack.appointment.api;

public interface NotificationService {
    String ping();
    void notifyApproval(Long patientId, String message);
    void notifyRejection(Long patientId, String message);
}
