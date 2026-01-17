package com.physiotrack.appointment.impl;

import com.physiotrack.appointment.api.AppointmentService;
import com.physiotrack.appointment.api.NotificationService;
import com.physiotrack.appointment.api.ScheduleService;
import com.physiotrack.appointment.api.model.Appointment;
import com.physiotrack.appointment.api.model.AppointmentRequestType;
import com.physiotrack.appointment.api.model.AppointmentStatus;
import com.physiotrack.appointment.impl.repository.AppointmentRepository;
import com.physiotrack.user.management.api.UserManagementService;
import com.physiotrack.user.management.api.model.User;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import java.time.LocalDateTime;
import java.util.List;

public class Activator implements BundleActivator {

    private ServiceRegistration<AppointmentService> apptReg;
    private ServiceRegistration<ScheduleService> schedReg;
    private ServiceRegistration<NotificationService> notifReg;

    @Override
    public void start(BundleContext context) {
        AppointmentRepository repo = new AppointmentRepository();

        // optional dependency: user-management service (for seeded IDs / validation)
        UserManagementService userSvc = getUserServiceIfAvailable(context);

        NotificationService notifSvc = new NotificationServiceImpl();
        ScheduleService schedSvc = new ScheduleServiceImpl(repo);
        AppointmentService apptSvc = new AppointmentServiceImpl(repo, userSvc, notifSvc);

        notifReg = context.registerService(NotificationService.class, notifSvc, null);
        schedReg = context.registerService(ScheduleService.class, schedSvc, null);
        apptReg = context.registerService(AppointmentService.class, apptSvc, null);

        // Seed 1 pending NEW request if possible
        seedAppointmentIfPossible(repo, userSvc);

        System.out.println("[appointment-impl] AppointmentService registered");
    }

    @Override
    public void stop(BundleContext context) {
        if (apptReg != null) apptReg.unregister();
        if (schedReg != null) schedReg.unregister();
        if (notifReg != null) notifReg.unregister();
        System.out.println("[appointment-impl] stopped");
    }

    private UserManagementService getUserServiceIfAvailable(BundleContext context) {
        ServiceReference<UserManagementService> ref = context.getServiceReference(UserManagementService.class);
        if (ref == null) return null;
        return context.getService(ref);
    }

    private void seedAppointmentIfPossible(AppointmentRepository repo, UserManagementService userSvc) {
        if (repo.count() > 0) return;

        if (userSvc == null) {
            System.out.println("[SEED] user-management not available; skipping appointment seeding.");
            return;
        }

        List<User> physios = userSvc.listByRole("PHYSIO");
        List<User> patients = userSvc.listByRole("PATIENT");

        if (physios.isEmpty() || patients.isEmpty()) {
            System.out.println("[SEED] Not enough users to seed appointment (need PHYSIO + PATIENT).");
            return;
        }

        User physio = physios.get(0);
        User patient = patients.get(0);

        Appointment req = new Appointment();
        req.setPatientId(patient.getId());
        req.setPhysioId(physio.getId());
        req.setDateTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
        req.setDetails("Knee pain, mild swelling");
        req.setRequestType(AppointmentRequestType.NEW);
        req.setStatus(AppointmentStatus.PENDING);

        LocalDateTime now = LocalDateTime.now();
        req.setCreatedAt(now);
        req.setUpdatedAt(now);

        repo.save(req);
        System.out.println("[SEED] Appointment requests seeded (1 pending NEW)");
    }
}
