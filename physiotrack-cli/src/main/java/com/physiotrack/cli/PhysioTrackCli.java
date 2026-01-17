package com.physiotrack.cli;

import com.physiotrack.appointment.api.AppointmentService;
import com.physiotrack.journal.api.JournalService;
import com.physiotrack.personal.info.api.PersonalInfoService;
import com.physiotrack.progress.tracking.api.ProgressTrackingService;
import com.physiotrack.summary.api.SummaryService;
import com.physiotrack.therapy.api.TherapyService;
import com.physiotrack.user.management.api.UserManagementService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(immediate = true)
public class PhysioTrackCli {

    // Optional references so CLI still starts even if a service bundle isn't active yet.
    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile AppointmentService appointmentService;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile TherapyService therapyService;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile JournalService journalService;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile SummaryService summaryService;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile ProgressTrackingService progressTrackingService;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile UserManagementService userManagementService;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile PersonalInfoService personalInfoService;

    @Activate
    void activate() {
        System.out.println("====================================");
        System.out.println(" PhysioTrack OSGi CLI started (DS) ");
        System.out.println("====================================");

        print("appointment", appointmentService);
        print("therapy", therapyService);
        print("journal", journalService);
        print("summary", summaryService);
        print("progress-tracking", progressTrackingService);
        print("user-management", userManagementService);
        print("personal-info", personalInfoService);

        System.out.println("\nNext step: plug in your menu simulation here.");
    }

    private void print(String name, Object svc) {
        System.out.printf("Service %-18s : %s%n", name, (svc == null ? "NOT AVAILABLE" : "AVAILABLE"));
        if (svc != null) {
            try {
                var m = svc.getClass().getMethod("ping");
                Object result = m.invoke(svc);
                System.out.printf("  -> ping(): %s%n", result);
            } catch (Exception ignored) {
                // ignore
            }
        }
    }
}
