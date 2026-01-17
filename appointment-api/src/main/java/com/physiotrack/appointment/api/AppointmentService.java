package com.physiotrack.appointment.api;

import com.physiotrack.appointment.api.model.Appointment;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {

    // keep for ping menu
    String ping();

    // UC15 patient booking request
    Appointment createBookingRequest(Long patientId, Long physioId, LocalDateTime dateTime, String details);

    // UC16 update request
    Appointment createUpdateRequest(Long patientId, Long targetAppointmentId, LocalDateTime newDateTime, String newDetails);

    // UC17 cancel request
    Appointment createCancelRequest(Long patientId, Long targetAppointmentId);

    // UC24 admin manage requests
    List<Appointment> listPendingNewRequests();
    List<Appointment> listPendingUpdateRequests();
    List<Appointment> listPendingCancelRequests();

    Appointment approveRequest(Long requestId);
    Appointment rejectRequest(Long requestId);

    // confirmed NEW appointments
    List<Appointment> listConfirmedAppointmentsForPatient(Long patientId);

    Appointment getAppointmentById(Long appointmentId);
}
