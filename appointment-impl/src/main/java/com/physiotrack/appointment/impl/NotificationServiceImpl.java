package com.physiotrack.appointment.impl;

import com.physiotrack.appointment.api.NotificationService;

public class NotificationServiceImpl implements NotificationService {

    @Override
    public String ping() {
        return "notification OK (console)";
    }

    @Override
    public void notifyApproval(Long patientId, String message) {
        System.out.println("[NOTIFY][APPROVED] patientId=" + patientId + " msg=" + message);
    }

    @Override
    public void notifyRejection(Long patientId, String message) {
        System.out.println("[NOTIFY][REJECTED] patientId=" + patientId + " msg=" + message);
    }
}
