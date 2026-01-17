package com.physiotrack.app;

import com.physiotrack.appointment.api.AppointmentService;
import com.physiotrack.journal.api.JournalService;
import com.physiotrack.personal.info.api.PersonalInfoService;
import com.physiotrack.progress.tracking.api.ProgressTrackingService;
import com.physiotrack.summary.api.SummaryService;
import com.physiotrack.therapy.api.TherapyService;
import com.physiotrack.user.management.api.UserManagementService;
import com.physiotrack.test.api.TestService;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import java.util.Dictionary;
import java.util.Hashtable;

public class Activator implements BundleActivator {

    private BundleContext ctx;
    private ServiceRegistration<?> reg;

    @Override
    public void start(BundleContext context) {
        this.ctx = context;

        // Register Gogo commands under scope "physio"
        Dictionary<String, Object> props = new Hashtable<>();
        props.put("osgi.command.scope", "physio");
        props.put("osgi.command.function", new String[] {
                "menu", "help", "ping", "pingall",
                "uc",
                "appt", "therapy", "journal", "summary", "progress", "user", "personal", "testuc"
        });

        reg = context.registerService(Object.class.getName(), new PhysioCommands(), props);

        System.out.println("[app-runner] Gogo commands registered. Try: physio:menu");
    }

    @Override
    public void stop(BundleContext context) {
        if (reg != null) reg.unregister();
        reg = null;
        ctx = null;
        System.out.println("[app-runner] stopped");
    }

    public class PhysioCommands {

        // -------------------------
        // Existing ping menu (kept)
        // -------------------------
        public void menu() {
            System.out.println("========= PhysioTrack OSGi (Gogo) =========");
            System.out.println("Health check commands:");
            System.out.println("  physio:ping <module>");
            System.out.println("  physio:pingall");
            System.out.println("Modules:");
            System.out.println("  appointment | therapy | journal | summary | progress | user | personal | test");
            System.out.println();
            System.out.println("Use-case commands:");
            System.out.println("  physio:uc");
            System.out.println("  physio:appt <send|edit|cancel|view|approve|decline> [args]");
            System.out.println("  physio:therapy   (TODO)");
            System.out.println("  physio:journal   (TODO)");
            System.out.println("  physio:summary   (TODO)");
            System.out.println("  physio:progress  (TODO)");
            System.out.println("  physio:user      (TODO)");
            System.out.println("  physio:personal  (TODO)");
            System.out.println("  physio:testuc    (TODO)");
            System.out.println("==========================================");
        }

        public void help() {
            menu();
        }

        public void ping(String module) {
            String m = module == null ? "" : module.trim().toLowerCase();

            switch (m) {
                case "appointment" -> pingService("Appointment", AppointmentService.class);
                case "therapy" -> pingService("Therapy", TherapyService.class);
                case "journal" -> pingService("Journal", JournalService.class);
                case "summary" -> pingService("Summary", SummaryService.class);
                case "progress", "progress-tracking" -> pingService("Progress-Tracking", ProgressTrackingService.class);
                case "user", "user-management" -> pingService("User-Management", UserManagementService.class);
                case "personal", "personal-info" -> pingService("Personal-Info", PersonalInfoService.class);
                case "test" -> pingService("Test", TestService.class);
                default -> {
                    System.out.println("Unknown module: " + module);
                    System.out.println("Try: physio:ping appointment|therapy|journal|summary|progress|user|personal|test");
                }
            }
        }

        public void pingall() {
            pingService("Appointment", AppointmentService.class);
            pingService("Therapy", TherapyService.class);
            pingService("Journal", JournalService.class);
            pingService("Summary", SummaryService.class);
            pingService("Progress-Tracking", ProgressTrackingService.class);
            pingService("User-Management", UserManagementService.class);
            pingService("Personal-Info", PersonalInfoService.class);
            pingService("Test", TestService.class);
        }

        private <T> void pingService(String label, Class<T> clazz) {
            if (ctx == null) {
                System.out.println("[" + label + "] ERROR: context not available");
                return;
            }

            ServiceReference<T> ref = ctx.getServiceReference(clazz);
            if (ref == null) {
                System.out.println("[" + label + "] NOT AVAILABLE (start the *-impl bundle)");
                return;
            }

            T svc = ctx.getService(ref);
            if (svc == null) {
                System.out.println("[" + label + "] NOT AVAILABLE (service instance null)");
                return;
            }

            try {
                Object out = svc.getClass().getMethod("ping").invoke(svc);
                System.out.println("[" + label + "] OK -> " + out);
            } catch (Exception e) {
                System.out.println("[" + label + "] ERROR calling ping(): " + e.getMessage());
            } finally {
                // release service usage
                ctx.ungetService(ref);
            }
        }

        // -------------------------
        // Use-case command index
        // -------------------------
        public void uc() {
            System.out.println("=========== Use Case Commands ===========");
            System.out.println("Appointment: physio:appt <action> [args]");
            System.out.println("  actions: send | edit | cancel | view | approve | decline");
            System.out.println("Therapy: physio:therapy");
            System.out.println("  actions: (put 1 action for each use case/test case/however u develop)");
            System.out.println("Journal: physio:journal");
            System.out.println("  actions: (put 1 action for each use case/test case/however u develop)");
            System.out.println("Summary: physio:summary");
            System.out.println("  actions: (put 1 action for each use case/test case/however u develop)");
            System.out.println("Progress: physio:progress");
            System.out.println("  actions: (put 1 action for each use case/test case/however u develop)");
            System.out.println("User Management: physio:user");
            System.out.println("  actions: (put 1 action for each use case/test case/however u develop)");
            System.out.println("Personal Info: physio:personal");
            System.out.println("  actions: (put 1 action for each use case/test case/however u develop)");
            System.out.println("First-time Screening Test: physio:testuc");
            System.out.println("  actions: (put 1 action for each use case/test case/however u develop)");
            System.out.println("=========================================");
        }

        // -------------------------
        // Appointment module templates (UC15/16/17/18/24 style)
        // -------------------------
        public void appt(String action, String... args) {
            String a = (action == null) ? "" : action.trim().toLowerCase();

            switch (a) {
                case "send" -> {
                    // Template: UC15 send booking request
                    System.out.println("[UC15] Send Booking Request (template)");
                    System.out.println("Expected args: <patientEmail> <physioEmail> <yyyy-mm-dd> <hh:mm> <reason>");
                    System.out.println("Example: physio:appt send p@x.com t@x.com 2026-01-20 10:30 \"knee pain\"");
                    // TODO: AppointmentService.sendBookingRequest(...)
                }
                case "edit" -> {
                    // Template: UC16 send edit request
                    System.out.println("[UC16] Send Edit Request (template)");
                    System.out.println("Expected args: <requestId> <newDate> <newTime> <newReason>");
                    System.out.println("Example: physio:appt edit REQ-001 2026-01-21 11:00 \"reschedule\"");
                    // TODO: AppointmentService.sendEditRequest(...)
                }
                case "cancel" -> {
                    // Template: UC17 send cancel request
                    System.out.println("[UC17] Send Cancel Request (template)");
                    System.out.println("Expected args: <requestId> <reason>");
                    System.out.println("Example: physio:appt cancel REQ-001 \"cannot attend\"");
                    // TODO: AppointmentService.sendCancelRequest(...)
                }
                case "view" -> {
                    // Template: UC18 view appointment schedule
                    System.out.println("[UC18] View Appointment Schedule (template)");
                    System.out.println("Expected args: <userEmail> [yyyy-mm-dd]");
                    System.out.println("Example: physio:appt view p@x.com 2026-01-20");
                    // TODO: AppointmentService.viewSchedule(...)
                }
                case "approve" -> {
                    // Template: UC24 admin approves request
                    System.out.println("[UC24] Admin Approve Request (template)");
                    System.out.println("Expected args: <adminEmail> <requestId>");
                    System.out.println("Example: physio:appt approve admin@x.com REQ-001");
                    // TODO: AppointmentService.approveRequest(...)
                }
                case "decline" -> {
                    // Template: UC24 admin declines request
                    System.out.println("[UC24] Admin Decline Request (template)");
                    System.out.println("Expected args: <adminEmail> <requestId> <reason>");
                    System.out.println("Example: physio:appt decline admin@x.com REQ-001 \"slot unavailable\"");
                    // TODO: AppointmentService.declineRequest(...)
                }
                default -> {
                    System.out.println("Unknown appointment action: " + action);
                    System.out.println("Try: physio:appt send|edit|cancel|view|approve|decline");
                }
            }
        }

        // -------------------------
        // Other modules: empty TODO stubs for now
        // -------------------------
        public void therapy(String... args) {
            System.out.println("[TODO] Therapy module CLI not implemented yet.");
            // TODO: Implement therapy commands + call TherapyService methods
        }

        public void journal(String... args) {
            System.out.println("[TODO] Journal module CLI not implemented yet.");
            // TODO: Implement journal commands + call JournalService methods
        }

        public void summary(String... args) {
            System.out.println("[TODO] Summary module CLI not implemented yet.");
            // TODO: Implement summary commands + call SummaryService methods
        }

        public void progress(String... args) {
            System.out.println("[TODO] Progress-Tracking module CLI not implemented yet.");
            // TODO: Implement progress commands + call ProgressTrackingService methods
        }

        public void user(String... args) {
            System.out.println("[TODO] User-Management module CLI not implemented yet.");
            // TODO: Implement user commands + call UserManagementService methods
        }

        public void personal(String... args) {
            System.out.println("[TODO] Personal-Info module CLI not implemented yet.");
            // TODO: Implement personal commands + call PersonalInfoService methods
        }

        public void testuc(String... args) {
            System.out.println("[TODO] Test module CLI not implemented yet.");
            // TODO: Implement test commands + call TestService methods
        }
    }
}
