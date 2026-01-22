package com.physiotrack.app;

import com.physiotrack.appointment.api.AppointmentService;
import com.physiotrack.appointment.api.ScheduleService;
import com.physiotrack.appointment.api.model.Appointment;
import com.physiotrack.journal.api.JournalService;
import com.physiotrack.personal.info.api.PersonalInfoService;
import com.physiotrack.progress.tracking.api.ProgressTrackingService;
import com.physiotrack.summary.api.SummaryService;
import com.physiotrack.therapy.api.TherapyManagementService;
import com.physiotrack.therapy.api.TherapyProgressService;
import com.physiotrack.therapy.api.model.OTActivity;
import com.physiotrack.therapy.api.model.OTProgram;
import com.physiotrack.therapy.api.model.PTActivity;
import com.physiotrack.therapy.api.model.PTProgram;
import com.physiotrack.user.management.api.UserManagementService;
import com.physiotrack.test.api.TestService;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Arrays;
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
            System.out.println("Run module commands:");
            System.out.println("  physio:appt <send|edit|cancel|view|approve|reject|pending> [args]");
            System.out.println("  physio:therapy <addpt|removept|addot|removeot|listpt|listot|markpt|markot> [args]");
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
                case "therapy" -> pingService("Therapy", TherapyManagementService.class);
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
            pingService("Therapy", TherapyManagementService.class);
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
                ctx.ungetService(ref);
            }
        }
        
        // -------------------------
        // Appointment module CLI (UC15/16/17/18/24)
        // -------------------------
        public void appt(String action, String... args) {
            String a = (action == null) ? "" : action.trim().toLowerCase();

            switch (a) {

                // UC15 - patient booking request
                case "send" -> withService(AppointmentService.class, svc -> {
                    if (args.length < 5) {
                        System.out.println("[UC15] Usage: physio:appt send <patientId> <physioId> <yyyy-mm-dd> <hh:mm> <details>");
                        System.out.println("Example: physio:appt send 4 2 2026-01-20 10:30 \"knee pain\"");
                        return;
                    }
                    Long patientId = parseLong(args[0], "patientId");
                    Long physioId = parseLong(args[1], "physioId");
                    LocalDateTime dt = parseDateTime(args[2], args[3]);
                    String details = joinFrom(args, 4);

                    Appointment created = svc.createBookingRequest(patientId, physioId, dt, details);
                    System.out.println("[UC15] Created booking request: " + formatAppointment(created));
                });

                // UC16 - patient edit request
                case "edit" -> withService(AppointmentService.class, svc -> {
                    if (args.length < 5) {
                        System.out.println("[UC16] Usage: physio:appt edit <patientId> <targetAppointmentId> <yyyy-mm-dd> <hh:mm> <newDetails>");
                        System.out.println("Example: physio:appt edit 4 1 2026-01-21 11:00 \"reschedule\"");
                        return;
                    }
                    Long patientId = parseLong(args[0], "patientId");
                    Long targetId = parseLong(args[1], "targetAppointmentId");
                    LocalDateTime dt = parseDateTime(args[2], args[3]);
                    String newDetails = joinFrom(args, 4);

                    Appointment created = svc.createUpdateRequest(patientId, targetId, dt, newDetails);
                    System.out.println("[UC16] Created update request: " + formatAppointment(created));
                });

                // UC17 - patient cancel request
                case "cancel" -> withService(AppointmentService.class, svc -> {
                    if (args.length < 2) {
                        System.out.println("[UC17] Usage: physio:appt cancel <patientId> <targetAppointmentId>");
                        System.out.println("Example: physio:appt cancel 4 1");
                        return;
                    }
                    Long patientId = parseLong(args[0], "patientId");
                    Long targetId = parseLong(args[1], "targetAppointmentId");

                    Appointment created = svc.createCancelRequest(patientId, targetId);
                    System.out.println("[UC17] Created cancel request: " + formatAppointment(created));
                });

                // UC18 - physio view schedule (approved appointments between dates)
                case "view" -> withService(ScheduleService.class, sched -> {
                    if (args.length < 3) {
                        System.out.println("[UC18] Usage: physio:appt view <physioId> <fromDate yyyy-mm-dd> <toDate yyyy-mm-dd>");
                        System.out.println("Example: physio:appt view 2 2026-01-01 2026-01-31");
                        return;
                    }
                    Long physioId = parseLong(args[0], "physioId");
                    LocalDate from = parseDate(args[1], "fromDate");
                    LocalDate to = parseDate(args[2], "toDate");

                    LocalDateTime fromDt = from.atStartOfDay();
                    LocalDateTime toDt = to.atTime(LocalTime.MAX);

                    List<Appointment> appts = sched.getScheduleForPhysio(physioId, fromDt, toDt);
                    System.out.println("[UC18] Approved schedule for physioId=" + physioId + " (" + appts.size() + " items)");
                    appts.forEach(x -> System.out.println("  - " + formatAppointment(x)));
                });

                // helper: list pending requests by type (useful for UC24 admin)
                case "pending" -> withService(AppointmentService.class, svc -> {
                    if (args.length < 1) {
                        System.out.println("Usage: physio:appt pending <new|update|cancel>");
                        return;
                    }
                    String type = args[0].trim().toLowerCase();
                    List<Appointment> pending;
                    switch (type) {
                        case "new" -> pending = svc.listPendingNewRequests();
                        case "update" -> pending = svc.listPendingUpdateRequests();
                        case "cancel" -> pending = svc.listPendingCancelRequests();
                        default -> {
                            System.out.println("Unknown type: " + args[0] + " (use new|update|cancel)");
                            return;
                        }
                    }
                    System.out.println("[PENDING] " + type.toUpperCase() + " requests: " + pending.size());
                    pending.forEach(x -> System.out.println("  - " + formatAppointment(x)));
                });

                // UC24 - admin approve request
                case "approve" -> withService(AppointmentService.class, svc -> {
                    if (args.length < 1) {
                        System.out.println("[UC24] Usage: physio:appt approve <requestId>");
                        System.out.println("Example: physio:appt approve 1");
                        return;
                    }
                    Long requestId = parseLong(args[0], "requestId");
                    Appointment updated = svc.approveRequest(requestId);
                    System.out.println("[UC24] Approved request: " + formatAppointment(updated));
                });

                // UC24 - admin reject request (REJECT not DECLINE)
                case "reject" -> withService(AppointmentService.class, svc -> {
                    if (args.length < 1) {
                        System.out.println("[UC24] Usage: physio:appt reject <requestId>");
                        System.out.println("Example: physio:appt reject 1");
                        return;
                    }
                    Long requestId = parseLong(args[0], "requestId");
                    Appointment updated = svc.rejectRequest(requestId);
                    System.out.println("[UC24] Rejected request: " + formatAppointment(updated));
                });

                default -> {
                    System.out.println("Unknown appointment action: " + action);
                    System.out.println("Try: physio:appt send|edit|cancel|view|pending|approve|reject");
                }
            }
        }

        // -------------------------
        // Therapy module CLI (UC11/12/20/21)
        // -------------------------
        public void therapy(String action, String... args) {
            String a = (action == null) ? "" : action.trim().toLowerCase();

            if (args.length < 2) {
                System.out.println("Usage: physio:therapy <action> <role> <userId> [otherArgs...]");
                return;
            }
            String role = args[0].trim().toUpperCase();
            Long userId = parseLong(args[1], "userId");
            String[] opArgs = Arrays.copyOfRange(args, 2, args.length);

            withService(TherapyManagementService.class, svc -> {
                switch (a) {
                    case "listpt": {
                        Long patientId = userId;
                        PTProgram ptProgram = svc.findPTProgramByPatientId(patientId);
                        if (ptProgram == null || ptProgram.getActivities() == null || ptProgram.getActivities().isEmpty()) {
                            System.out.println("No PT activities for patient " + patientId);
                        } else {
                            System.out.println("PT Activities for patient " + patientId + ":");
                            for (int i = 0; i < ptProgram.getActivities().size(); i++) {
                                PTActivity aObj = ptProgram.getActivities().get(i);
                                System.out.println(" " + (i + 1) + ") " + aObj.getName() + " | Completed: " + aObj.isCompleted());
                            }
                        }
                        break;
                    }
                    case "listot": {
                        Long patientId = userId;
                        OTProgram otProgram = svc.findOTProgramByPatientId(patientId);
                        if (otProgram == null || otProgram.getActivities() == null || otProgram.getActivities().isEmpty()) {
                            System.out.println("No OT activities for patient " + patientId);
                        } else {
                            System.out.println("OT Activities for patient " + patientId + ":");
                            for (int i = 0; i < otProgram.getActivities().size(); i++) {
                                OTActivity aObj = otProgram.getActivities().get(i);
                                System.out.println(" " + (i + 1) + ") " + aObj.getName() + " | Completed: " + aObj.isCompleted());
                            }
                        }
                        break;
                    }
                    case "removept": {
                        if (opArgs.length < 1) {
                            System.out.println("Usage: physio:therapy removept PHYSIO <patientId> <activityIndex>");
                            return;
                        }
                        Long patientId = userId;
                        int ptIndex = Integer.parseInt(opArgs[1]) - 1;
                        PTProgram ptProgram = svc.findPTProgramByPatientId(patientId);
                        if (ptProgram == null || ptProgram.getActivities() == null || ptProgram.getActivities().isEmpty()) {
                            System.out.println("No PT activities to remove for patient " + patientId);
                        } else if (ptIndex < 0 || ptIndex >= ptProgram.getActivities().size()) {
                            System.out.println("Invalid activity index.");
                        } else {
                            PTActivity toRemove = ptProgram.getActivities().get(ptIndex);
                            svc.removePTActivity(ptProgram.getPatientId(), toRemove.getId());
                            System.out.println("Removed PT activity '" + toRemove.getName() + "' for patient " + patientId);
                        }
                        break;
                    }
                    case "removeot": {
                        if (opArgs.length < 1) {
                            System.out.println("Usage: physio:therapy removeot PHYSIO <patientId> <activityIndex>");
                            return;
                        }
                        Long patientId = userId;
                        int otIndex = Integer.parseInt(opArgs[1]) - 1;
                        OTProgram otProgram = svc.findOTProgramByPatientId(patientId);
                        if (otProgram == null || otProgram.getActivities() == null || otProgram.getActivities().isEmpty()) {
                            System.out.println("No OT activities to remove for patient " + patientId);
                        } else if (otIndex < 0 || otIndex >= otProgram.getActivities().size()) {
                            System.out.println("Invalid activity index.");
                        } else {
                            OTActivity toRemove = otProgram.getActivities().get(otIndex);
                            svc.removeOTActivity(otProgram.getPatientId(), toRemove.getId());
                            System.out.println("Removed OT activity '" + toRemove.getName() + "' for patient " + patientId);
                        }
                        break;
                    }
                    case "addpt": {
                        if (opArgs.length < 1) {
                            System.out.println("Usage: physio:therapy addpt PHYSIO <patientId> <activityName>");
                            return;
                        }
                        Long patientId = userId;
                        String activityName = joinFrom(opArgs, 0);
                        PTProgram ptProgram = svc.findPTProgramByPatientId(patientId);
                        if (ptProgram == null) {
                            ptProgram = new com.physiotrack.therapy.api.model.PTProgram();
                            ptProgram.setPatientId(patientId);
                            svc.savePTProgram(ptProgram);
                        }
                        Long ptProgramId = ptProgram.getPatientId();
                        PTActivity ptActivity = new PTActivity();
                        ptActivity.setName(activityName);
                        ptActivity.setCompleted(false);
                        svc.addPTActivity(ptProgramId, ptActivity);
                        System.out.println("Added PT activity '" + activityName + "' for patient " + patientId);
                        break;
                    }
                    case "addot": {
                        if (opArgs.length < 1) {
                            System.out.println("Usage: physio:therapy addot PHYSIO <patientId> <activityName>");
                            return;
                        }
                        Long patientId = userId;
                        String activityName = joinFrom(opArgs, 0);
                        OTProgram otProgram = svc.findOTProgramByPatientId(patientId);
                        if (otProgram == null) {
                            otProgram = new com.physiotrack.therapy.api.model.OTProgram();
                            otProgram.setPatientId(patientId);
                            svc.saveOTProgram(otProgram);
                        }
                        Long otProgramId = otProgram.getPatientId();
                        OTActivity otActivity = new OTActivity();
                        otActivity.setName(activityName);
                        otActivity.setCompleted(false);
                        svc.addOTActivity(otProgramId, otActivity);
                        System.out.println("Added OT activity '" + activityName + "' for patient " + patientId);
                        break;
                    }
                    case "markpt": {
                        if (!"PATIENT".equals(role)) {
                            System.out.println("Only patients can mark PT activities as completed.");
                            return;
                        }
                        if (opArgs.length < 1) {
                            System.out.println("Usage: physio:therapy markpt PATIENT <programId> <activityIndex>");
                            return;
                        }
                        Long programId = parseLong(opArgs[0], "programId");
                        int activityIndex = Integer.parseInt(opArgs[1]) - 1;
                        withService(TherapyProgressService.class, progressSvc -> {
                            List<PTActivity> activities = progressSvc.getPTActivities(programId);
                            if (activities == null || activities.isEmpty()) {
                                System.out.println("No PT activities to mark for this program.");
                                return;
                            }
                            if (activityIndex < 0 || activityIndex >= activities.size()) {
                                System.out.println("Invalid activity index.");
                                return;
                            }
                            PTActivity selectedActivity = activities.get(activityIndex);
                            if (selectedActivity.isCompleted()) {
                                System.out.println("Activity is already marked as completed.");
                            } else {
                                progressSvc.markPTCompleted(programId, selectedActivity.getId());
                                System.out.println("Marked PT activity '" + selectedActivity.getName() + "' as completed.");
                            }
                        });
                        break;
                    }
                    case "markot": {
                        if (!"PATIENT".equals(role)) {
                            System.out.println("Only patients can mark OT activities as completed.");
                            return;
                        }
                        if (opArgs.length < 1) {
                            System.out.println("Usage: physio:therapy markot PATIENT <programId> <activityIndex>");
                            return;
                        }
                        Long programId = parseLong(opArgs[0], "programId");
                        int activityIndex = Integer.parseInt(opArgs[1]) - 1;
                        withService(TherapyProgressService.class, progressSvc -> {
                            List<OTActivity> activities = progressSvc.getOTActivities(programId);
                            if (activities == null || activities.isEmpty()) {
                                System.out.println("No OT activities to mark for this program.");
                                return;
                            }
                            if (activityIndex < 0 || activityIndex >= activities.size()) {
                                System.out.println("Invalid activity index.");
                                return;
                            }
                            OTActivity selectedActivity = activities.get(activityIndex);
                            if (selectedActivity.isCompleted()) {
                                System.out.println("Activity is already marked as completed.");
                            } else {
                                progressSvc.markOTCompleted(programId, selectedActivity.getId());
                                System.out.println("Marked OT activity '" + selectedActivity.getName() + "' as completed.");
                            }
                        });
                        break;
                    }
                    default:
                        System.out.println("Unknown therapy action: " + action);
                        System.out.println("Try: physio:therapy addpt|removept|listpt|addot|removeot|listot|markpt|markot ...");
                }
            });
        }
        
        // -------------------------
        // Other modules: empty TODO stubs for now
        // -------------------------
        public void journal(String... args) {
            System.out.println("[TODO] Journal module CLI not implemented yet.");
        }

        public void summary(String... args) {
            System.out.println("[TODO] Summary module CLI not implemented yet.");
        }

        public void progress(String... args) {
            System.out.println("[TODO] Progress-Tracking module CLI not implemented yet.");
        }

        public void user(String... args) {
            System.out.println("[TODO] User-Management module CLI not implemented yet.");
        }

        public void personal(String... args) {
            System.out.println("[TODO] Personal-Info module CLI not implemented yet.");
        }

        public void testuc(String... args) {
            System.out.println("[TODO] Test module CLI not implemented yet.");
        }

        // -------------------------
        // Helpers
        // -------------------------
        private <T> void withService(Class<T> clazz, ServiceConsumer<T> consumer) {
            if (ctx == null) {
                System.out.println("ERROR: BundleContext not available.");
                return;
            }
            ServiceReference<T> ref = ctx.getServiceReference(clazz);
            if (ref == null) {
                System.out.println("Service not available: " + clazz.getSimpleName() + " (start the *-impl bundle)");
                return;
            }
            T svc = ctx.getService(ref);
            if (svc == null) {
                System.out.println("Service instance null: " + clazz.getSimpleName());
                return;
            }

            try {
                consumer.accept(svc);
            } catch (IllegalArgumentException ex) {
                System.out.println("ERROR: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            } finally {
                ctx.ungetService(ref);
            }
        }

        @FunctionalInterface
        private interface ServiceConsumer<T> {
            void accept(T svc);
        }

        private Long parseLong(String s, String field) {
            try {
                return Long.parseLong(s.trim());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid " + field + ": " + s);
            }
        }

        private LocalDate parseDate(String s, String field) {
            try {
                return LocalDate.parse(s.trim());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid " + field + " (expected yyyy-mm-dd): " + s);
            }
        }

        private LocalDateTime parseDateTime(String date, String time) {
            try {
                LocalDate d = LocalDate.parse(date.trim());
                LocalTime t = LocalTime.parse(time.trim());
                return LocalDateTime.of(d, t);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date/time (expected yyyy-mm-dd hh:mm): " + date + " " + time);
            }
        }

        private String joinFrom(String[] args, int start) {
            if (args.length <= start) return "";
            StringBuilder sb = new StringBuilder();
            for (int i = start; i < args.length; i++) {
                if (i > start) sb.append(' ');
                sb.append(args[i]);
            }
            return sb.toString().trim();
        }

        private String formatAppointment(Appointment a) {
            if (a == null) return "(null)";
            // keep formatting robust even if some getters not present
            try {
                Long id = a.getId();
                return "id=" + id
                        + " type=" + a.getRequestType()
                        + " status=" + a.getStatus()
                        + " patientId=" + a.getPatientId()
                        + " physioId=" + a.getPhysioId()
                        + " dateTime=" + a.getDateTime()
                        + (a.getTargetAppointmentId() != null ? " targetId=" + a.getTargetAppointmentId() : "")
                        + (a.getDetails() != null ? " details=\"" + a.getDetails() + "\"" : "");
            } catch (Exception e) {
                return a.toString();
            }
        }
    }
}
