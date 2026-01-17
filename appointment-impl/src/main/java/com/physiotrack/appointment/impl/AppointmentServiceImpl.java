package com.physiotrack.appointment.impl;

import com.physiotrack.appointment.api.AppointmentService;
import com.physiotrack.appointment.api.NotificationService;
import com.physiotrack.appointment.api.model.Appointment;
import com.physiotrack.appointment.api.model.AppointmentRequestType;
import com.physiotrack.appointment.api.model.AppointmentStatus;
import com.physiotrack.appointment.impl.repository.AppointmentRepository;
import com.physiotrack.user.management.api.UserManagementService;
import com.physiotrack.user.management.api.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository repo;
    private final UserManagementService userSvc; // may be null if not available
    private final NotificationService notifySvc;

    public AppointmentServiceImpl(AppointmentRepository repo,
                                 UserManagementService userSvc,
                                 NotificationService notifySvc) {
        this.repo = repo;
        this.userSvc = userSvc;
        this.notifySvc = notifySvc;
    }

    @Override
    public String ping() {
        return "appointment module OK (requests=" + repo.count() + ")";
    }

    @Override
    public Appointment createBookingRequest(Long patientId, Long physioId, LocalDateTime dateTime, String details) {
        validateUserIds(patientId, physioId);

        Appointment a = new Appointment();
        a.setPatientId(patientId);
        a.setPhysioId(physioId);
        a.setDateTime(dateTime);
        a.setDetails(details);
        a.setRequestType(AppointmentRequestType.NEW);
        a.setStatus(AppointmentStatus.PENDING);

        stampCreate(a);
        return repo.save(a);
    }

    @Override
    public Appointment createUpdateRequest(Long patientId, Long targetAppointmentId, LocalDateTime newDateTime, String newDetails) {
        // ensure target exists (optional strict check)
        Appointment target = repo.findById(targetAppointmentId);
        if (target == null) throw new IllegalArgumentException("Target appointment not found: " + targetAppointmentId);

        Appointment a = new Appointment();
        a.setPatientId(patientId);
        a.setPhysioId(target.getPhysioId());
        a.setDateTime(newDateTime);
        a.setDetails(newDetails);
        a.setTargetAppointmentId(targetAppointmentId);
        a.setRequestType(AppointmentRequestType.UPDATE);
        a.setStatus(AppointmentStatus.PENDING);

        stampCreate(a);
        return repo.save(a);
    }

    @Override
    public Appointment createCancelRequest(Long patientId, Long targetAppointmentId) {
        Appointment target = repo.findById(targetAppointmentId);
        if (target == null) throw new IllegalArgumentException("Target appointment not found: " + targetAppointmentId);

        Appointment a = new Appointment();
        a.setPatientId(patientId);
        a.setPhysioId(target.getPhysioId());
        a.setTargetAppointmentId(targetAppointmentId);
        a.setRequestType(AppointmentRequestType.CANCEL);
        a.setStatus(AppointmentStatus.PENDING);

        stampCreate(a);
        return repo.save(a);
    }

    @Override
    public List<Appointment> listPendingNewRequests() {
        return repo.findPendingByType(AppointmentRequestType.NEW);
    }

    @Override
    public List<Appointment> listPendingUpdateRequests() {
        return repo.findPendingByType(AppointmentRequestType.UPDATE);
    }

    @Override
    public List<Appointment> listPendingCancelRequests() {
        return repo.findPendingByType(AppointmentRequestType.CANCEL);
    }

    @Override
    public Appointment approveRequest(Long requestId) {
        Appointment req = repo.findById(requestId);
        if (req == null) throw new IllegalArgumentException("Request not found: " + requestId);

        req.setStatus(AppointmentStatus.APPROVED);
        stampUpdate(req);
        repo.save(req);

        if (notifySvc != null) {
            notifySvc.notifyApproval(req.getPatientId(), "Your appointment request (" + requestId + ") was approved.");
        }
        return req;
    }

    @Override
    public Appointment rejectRequest(Long requestId) {
        Appointment req = repo.findById(requestId);
        if (req == null) throw new IllegalArgumentException("Request not found: " + requestId);

        req.setStatus(AppointmentStatus.REJECTED);
        stampUpdate(req);
        repo.save(req);

        if (notifySvc != null) {
            notifySvc.notifyRejection(req.getPatientId(), "Your appointment request (" + requestId + ") was rejected.");
        }
        return req;
    }

    @Override
    public List<Appointment> listConfirmedAppointmentsForPatient(Long patientId) {
        // “confirmed” = APPROVED requests
        // (You can refine later to separate request rows vs actual appointments)
        return repo.findByPatientId(patientId).stream()
                .filter(a -> a.getStatus() == AppointmentStatus.APPROVED)
                .toList();
    }

    @Override
    public Appointment getAppointmentById(Long appointmentId) {
        return repo.findById(appointmentId);
    }

    private void stampCreate(Appointment a) {
        LocalDateTime now = LocalDateTime.now();
        a.setCreatedAt(now);
        a.setUpdatedAt(now);
    }

    private void stampUpdate(Appointment a) {
        a.setUpdatedAt(LocalDateTime.now());
    }

    private void validateUserIds(Long patientId, Long physioId) {
        if (patientId == null || physioId == null) return;
        if (userSvc == null) return; // allow running without user service, but less strict

        User p = userSvc.findById(patientId);
        User t = userSvc.findById(physioId);

        if (p == null) throw new IllegalArgumentException("Patient not found: " + patientId);
        if (t == null) throw new IllegalArgumentException("Physio not found: " + physioId);
    }
}
