package com.physiotrack.app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import com.physiotrack.appointment.api.AppointmentService;
import com.physiotrack.appointment.api.ScheduleService;
import com.physiotrack.appointment.api.model.Appointment;
import com.physiotrack.journal.api.JournalService;
import com.physiotrack.journal.api.model.Journal;
import com.physiotrack.personal.info.api.PersonalInfoService;
import com.physiotrack.progress.tracking.api.ProgressTrackingService;
import com.physiotrack.progress.tracking.api.model.TreatmentReport;
import com.physiotrack.summary.api.SummaryService;
import com.physiotrack.therapy.api.TherapyManagementService;
import com.physiotrack.therapy.api.TherapyProgressService;
import com.physiotrack.therapy.api.model.OTActivity;
import com.physiotrack.therapy.api.model.OTProgram;
import com.physiotrack.therapy.api.model.PTActivity;
import com.physiotrack.therapy.api.model.PTProgram;
import com.physiotrack.user.management.api.UserManagementService;
import com.physiotrack.user.management.api.model.User;
import com.physiotrack.test.api.TestManageService;
import com.physiotrack.test.api.TestService;
import com.physiotrack.test.api.model.Question;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

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
                "appt", "therapy", "journal", "summary", "progress", "user", "personal", "test"
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
            System.out.println("  physio:therapy list <pt|ot> <PHYSIO|PATIENT> <patientId>");
            System.out.println("  physio:therapy add <pt|ot> PHYSIO <patientId> <activityName>");
            System.out.println("  physio:therapy remove <pt|ot> PHYSIO <patientId> <activityIndex>");
            System.out.println("  physio:therapy mark <pt|ot> PATIENT <patientId> <activityIndex>");
            System.out.println("  physio:journal <create|view|list|edit|delete|share>");
            System.out.println("  physio:summary <view|badges|badge>");
            System.out.println("  physio:progress  <patients|details|reports|create> [args]");
            System.out.println("  physio:user create <username> <email> <role> [clinic]");
            System.out.println("  physio:user list");
            System.out.println("  physio:user role <role>");
            System.out.println("  physio:user assign <patientId> <physioId>");
            System.out.println("  physio:user patients <physioId>");
            System.out.println("  physio:user deactivate <email>");
            System.out.println("  physio:personal update <userId> <address> <phone>");
            System.out.println("  physio:personal lang <userId> <langCode>");
            System.out.println("  physio:test   <evaluate|questionlist|add|edit|remove> [args]");
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
                case "test" -> {
                    pingService("Test", TestService.class);
                    pingService("TestManage", TestManageService.class);
                }
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
            pingService("TestManage", TestManageService.class);
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

            if (args.length < 3) {
                System.out.println("Usage:");
                System.out.println("  physio:therapy list <pt|ot> <PHYSIO|PATIENT> <patientId>");
                System.out.println("  physio:therapy add <pt|ot> PHYSIO <patientId> <activityName>");
                System.out.println("  physio:therapy remove <pt|ot> PHYSIO <patientId> <activityIndex>");
                System.out.println("  physio:therapy mark <pt|ot> PATIENT <programId> <activityIndex>");
                return;
            }

            String type = args[0].toLowerCase();
            String role = args[1].toUpperCase();
            Long mainId = parseLong(args[2], "id");
            String[] opArgs = Arrays.copyOfRange(args, 3, args.length);

            if (!type.equals("pt") && !type.equals("ot")) {
                System.out.println("Invalid type. Use pt or ot.");
                return;
            }

            if (action.equals("list") || action.equals("add") || action.equals("remove")) {
                final boolean[] ok = { true };

                withService(UserManagementService.class, userSvc -> {
                    User u = userSvc.findById(mainId);
                    if (u == null || !"PATIENT".equalsIgnoreCase(u.getRole())) {
                        System.out.println("ERROR: User " + mainId + " is not a PATIENT.");
                        ok[0] = false;
                    }
                });

                if (!ok[0]) return;
            }

            withService(TherapyManagementService.class, svc -> {

                switch (action) {

                    //UC11 & UC12  View daily pt/ot activities

                    case "list": {
                        if ("pt".equals(type)) {
                            PTProgram ptProgram = svc.findPTProgramByPatientId(mainId);
                            if (ptProgram == null || ptProgram.getActivities().isEmpty()) {
                                System.out.println("No PT activities for patient " + mainId);
                            } else {
                                System.out.println("PT Activities for patient " + mainId + ":");
                                for (int i = 0; i < ptProgram.getActivities().size(); i++) {
                                    PTActivity a = ptProgram.getActivities().get(i);
                                    System.out.println(" " + (i + 1) + ") " + a.getName()
                                            + " | Completed: " + a.isCompleted());
                                }
                            }
                        } else {
                            OTProgram otProgram = svc.findOTProgramByPatientId(mainId);
                            if (otProgram == null || otProgram.getActivities().isEmpty()) {
                                System.out.println("No OT activities for patient " + mainId);
                            } else {
                                System.out.println("OT Activities for patient " + mainId + ":");
                                for (int i = 0; i < otProgram.getActivities().size(); i++) {
                                    OTActivity a = otProgram.getActivities().get(i);
                                    System.out.println(" " + (i + 1) + ") " + a.getName()
                                            + " | Completed: " + a.isCompleted());
                                }
                            }
                        }
                        break;
                    }

                    //UC20 & UC21  Modify pt/ot activities (Add)

                    case "add": {
                        if (!"PHYSIO".equals(role)) {
                            System.out.println("Only PHYSIO can add activities.");
                            return;
                        }
                        if (opArgs.length < 1) {
                            System.out.println("Usage: physio:therapy add <pt|ot> PHYSIO <patientId> <activityName>");
                            return;
                        }

                        String activityName = joinFrom(opArgs, 0);

                        if ("pt".equals(type)) {
                            PTProgram ptProgram = svc.findPTProgramByPatientId(mainId);
                            if (ptProgram == null) {
                                ptProgram = new PTProgram();
                                ptProgram.setPatientId(mainId);
                                svc.savePTProgram(ptProgram);
                            }

                            PTActivity activity = new PTActivity();
                            activity.setName(activityName);
                            activity.setCompleted(false);
                            svc.addPTActivity(mainId, activity);
                        } else {
                            OTProgram otProgram = svc.findOTProgramByPatientId(mainId);
                            if (otProgram == null) {
                                otProgram = new OTProgram();
                                otProgram.setPatientId(mainId);
                                svc.saveOTProgram(otProgram);
                            }

                            OTActivity activity = new OTActivity();
                            activity.setName(activityName);
                            activity.setCompleted(false);
                            svc.addOTActivity(mainId, activity);
                        }

                        System.out.println("Added " + type.toUpperCase() + " activity '" + activityName + "'");
                        break;
                    }

                    //UC22 & UC23  Modify pt/ot activities (Remove)

                    case "remove": {
                        if (!"PHYSIO".equals(role)) {
                            System.out.println("Only PHYSIO can remove activities.");
                            return;
                        }
                        if (opArgs.length < 1) {
                            System.out.println("Usage: physio:therapy remove <pt|ot> PHYSIO <patientId> <activityIndex>");
                            return;
                        }

                        int index = Integer.parseInt(opArgs[0]) - 1;

                        if ("pt".equals(type)) {
                            PTProgram ptProgram = svc.findPTProgramByPatientId(mainId);
                            if (ptProgram == null || index < 0 || index >= ptProgram.getActivities().size()) {
                                System.out.println("Invalid activity index.");
                                return;
                            }
                            PTActivity toRemove = ptProgram.getActivities().get(index);
                            svc.removePTActivity(mainId, toRemove.getId());
                            System.out.println("Removed PT activity '" + toRemove.getName() + "'");
                        } else {
                            OTProgram otProgram = svc.findOTProgramByPatientId(mainId);
                            if (otProgram == null || index < 0 || index >= otProgram.getActivities().size()) {
                                System.out.println("Invalid activity index.");
                                return;
                            }
                            OTActivity toRemove = otProgram.getActivities().get(index);
                            svc.removeOTActivity(mainId, toRemove.getId());
                            System.out.println("Removed OT activity '" + toRemove.getName() + "'");
                        }
                        break;
                    }

                    //UC11 & UC12  View pt/ot activities (Mark)

                    case "mark": {
                        if (!"PATIENT".equals(role)) {
                            System.out.println("Only PATIENT can mark activities.");
                            return;
                        }
                        if (opArgs.length < 1) {
                            System.out.println("Usage: physio:therapy mark <pt|ot> PATIENT <programId> <activityIndex>");
                            return;
                        }

                        int index = Integer.parseInt(opArgs[0]) - 1;

                        withService(TherapyProgressService.class, progressSvc -> {
                            if ("pt".equals(type)) {
                                List<PTActivity> activities = progressSvc.getPTActivities(mainId);
                                if (activities == null || index < 0 || index >= activities.size()) {
                                    System.out.println("Invalid activity index.");
                                    return;
                                }
                                PTActivity a = activities.get(index);
                                if (a.isCompleted()) {
                                    System.out.println("Activity already completed.");
                                } else {
                                    progressSvc.markPTCompleted(mainId, a.getId());
                                    System.out.println("Marked PT activity '" + a.getName() + "' as completed.");
                                }
                            } else {
                                List<OTActivity> activities = progressSvc.getOTActivities(mainId);
                                if (activities == null || index < 0 || index >= activities.size()) {
                                    System.out.println("Invalid activity index.");
                                    return;
                                }
                                OTActivity a = activities.get(index);
                                if (a.isCompleted()) {
                                    System.out.println("Activity already completed.");
                                } else {
                                    progressSvc.markOTCompleted(mainId, a.getId());
                                    System.out.println("Marked OT activity '" + a.getName() + "' as completed.");
                                }
                            }
                        });
                        break;
                    }

                    default:
                        System.out.println("Unknown therapy action: " + action);
                }
            });
        }

        // -------------------------
        // Other modules: empty TODO stubs for now
        // -------------------------
        public void journal(String... args) {
            String action = (args.length > 0 && args[0] != null) ? args[0].trim().toLowerCase() : "";

            switch (action) {
                case "create" -> withService(JournalService.class, svc -> {
                    if (args.length < 3) {
                        System.out.println("Usage: physio:journal create <patientId> <title> [comment]");
                        return;
                    }
                    Long patientId = parseLong(args[1], "patientId");
                    String title = args[2];
                    String comment = args.length > 3 ? joinFrom(args, 3) : "";

                    Journal j = new Journal();
                    j.setTitle(title);
                    j.setComment(comment);

                    Journal created = svc.createJournal(patientId, j);
                    System.out.println("Created journal: " + formatJournal(created));
                });

                case "view" -> withService(JournalService.class, svc -> {
                    if (args.length < 2) {
                        System.out.println("Usage: physio:journal view <journalId>");
                        return;
                    }
                    Long id = parseLong(args[1], "journalId");
                    Journal j = svc.getJournalById(id);
                    System.out.println(formatJournal(j));
                });

                case "list" -> withService(JournalService.class, svc -> {
                    if (args.length < 2) {
                        System.out.println("Usage: physio:journal list <patientId>");
                        return;
                    }
                    Long patientId = parseLong(args[1], "patientId");
                    svc.listJournalsForPatient(patientId).forEach(x -> System.out.println("  - " + formatJournal(x)));
                });

                case "edit" -> withService(JournalService.class, svc -> {
                    if (args.length < 4) {
                        System.out.println("Usage: physio:journal edit <patientId> <journalId> <newTitle> [newComment]");
                        return;
                    }
                    Long patientId = parseLong(args[1], "patientId");
                    Long journalId = parseLong(args[2], "journalId");
                    String newTitle = args[3];
                    String newComment = args.length > 4 ? joinFrom(args, 4) : null;

                    Journal upd = new Journal();
                    upd.setTitle(newTitle);
                    if (newComment != null) upd.setComment(newComment);

                    Journal out = svc.updateJournal(patientId, journalId, upd);
                    System.out.println("Updated: " + formatJournal(out));
                });

                case "delete" -> withService(JournalService.class, svc -> {
                    if (args.length < 3) {
                        System.out.println("Usage: physio:journal delete <patientId> <journalId>");
                        return;
                    }
                    Long patientId = parseLong(args[1], "patientId");
                    Long journalId = parseLong(args[2], "journalId");
                    boolean ok = svc.deleteJournal(patientId, journalId);
                    System.out.println(ok ? "Deleted" : "Delete failed");
                });

                case "share" -> withService(JournalService.class, svc -> {
                    if (args.length < 3) {
                        System.out.println("Usage: physio:journal share <journalId> <true|false>");
                        return;
                    }
                    Long journalId = parseLong(args[1], "journalId");
                    boolean share = Boolean.parseBoolean(args[2]);
                    Journal out = svc.setSharePermission(journalId, share);
                    System.out.println("Updated share: " + (out != null && out.isSharedWithPhysio()));
                });

                case "" -> System.out.println("Usage: physio:journal <create|view|list|edit|delete|share> ...");

                default -> System.out.println("Unknown journal action: " + action);
            }
        }

        private String formatJournal(Journal j) {
            if (j == null) return "(null)";
            try {
                return "id=" + j.getId()
                        + " patientId=" + j.getPatientId()
                        + " title=\"" + (j.getTitle() == null ? "" : j.getTitle()) + "\""
                        + (j.getComment() != null ? " comment=\"" + j.getComment() + "\"" : "")
                        + " shared=" + j.isSharedWithPhysio();
            } catch (Exception e) {
                return j.toString();
            }
        }

        public void summary(String... args) {
            String action = (args.length > 0 && args[0] != null) ? args[0].trim().toLowerCase() : "";

            ServiceReference<SummaryService> ref = null;
            Object svc = null;
            try {
                ref = ctx.getServiceReference(SummaryService.class);
                if (ref == null) {
                    System.out.println("Service not available: SummaryService (start the summary-impl bundle)");
                    return;
                }
                svc = ctx.getService(ref);
                if (svc == null) {
                    System.out.println("Service instance null: SummaryService");
                    return;
                }

                switch (action) {
                    case "view": {
                        if (args.length < 2) {
                            System.out.println("Usage: physio:summary view <patientId> [yyyy-mm]");
                            return;
                        }
                        Long patientId = parseLong(args[1], "patientId");
                        int year;
                        int month;
                        if (args.length >= 3) {
                            String[] ym = args[2].split("-");
                            if (ym.length != 2) {
                                System.out.println("Invalid year-month. Expected yyyy-mm");
                                return;
                            }
                            year = Integer.parseInt(ym[0]);
                            month = Integer.parseInt(ym[1]);
                        } else {
                            LocalDate now = LocalDate.now();
                            year = now.getYear();
                            month = now.getMonthValue();
                        }

                        // Try text-based summary service first (avoids API classloader mismatch)
                        try {
                            ServiceReference<?> textRef = ctx.getServiceReference("com.physiotrack.summary.text.TextSummaryService");
                            if (textRef != null) {
                                Object textSvc = ctx.getService(textRef);
                                try {
                                    java.lang.reflect.Method mtext = textSvc.getClass().getMethod("getMonthlySummaryText", Long.class, int.class, int.class);
                                    Object txt = mtext.invoke(textSvc, patientId, year, month);
                                    System.out.println(txt == null ? "(no summary)" : txt.toString());
                                    return;
                                } catch (NoSuchMethodException nsme) {
                                    // fallback to API reflective call
                                } finally {
                                    ctx.ungetService(textRef);
                                }
                            }

                        } catch (Throwable t) {
                            System.out.println("TextSummaryService attempt failed:");
                            t.printStackTrace(System.out);
                        }

                        try {
                            java.lang.reflect.Method m;
                            try {
                                m = svc.getClass().getMethod("getMonthlyProgress", Long.class, int.class, int.class);
                            } catch (NoSuchMethodException nsme) {
                                throw nsme;
                            }
                            Object p = m.invoke(svc, patientId, year, month);
                            if (p == null) {
                                System.out.println("No summary available for patient " + patientId);
                                return;
                            }
                            java.lang.reflect.Method gmYear = p.getClass().getMethod("getYear");
                            java.lang.reflect.Method gmMonth = p.getClass().getMethod("getMonth");
                            java.lang.reflect.Method gmTotal = p.getClass().getMethod("getTotalSessions");
                            java.lang.reflect.Method gmCompleted = p.getClass().getMethod("getCompletedSessions");
                            java.lang.reflect.Method gmRate = p.getClass().getMethod("getCompletionRate");
                            Object py = gmYear.invoke(p);
                            Object pm = gmMonth.invoke(p);
                            Object ptotal = gmTotal.invoke(p);
                            Object pcomp = gmCompleted.invoke(p);
                            Object prate = gmRate.invoke(p);

                            System.out.println("Monthly Summary for patient=" + patientId + " " + py + "-" + String.format("%02d", pm));
                            System.out.println("  totalSessions=" + ptotal + " completed=" + pcomp + " completionRate=" + prate + "%");

                            java.lang.reflect.Method gmBadges = p.getClass().getMethod("getBadges");
                            Object badgesObj = gmBadges.invoke(p);
                            if (badgesObj instanceof java.util.List list && !list.isEmpty()) {
                                System.out.println("  badges:");
                                for (Object b : list) {
                                    System.out.println("    - " + formatBadgeReflect(b));
                                }
                            } else {
                                System.out.println("  badges: (none)");
                            }
                        } catch (Throwable e) {
                            System.out.println("Exception while invoking SummaryService.getMonthlyProgress:");
                            e.printStackTrace(System.out);
                        }
                        break;
                    }

                    case "badges": {
                        if (args.length < 2) {
                            System.out.println("Usage: physio:summary badges <patientId> [yyyy-mm]");
                            return;
                        }
                        Long patientId = parseLong(args[1], "patientId");
                        int year;
                        int month;
                        if (args.length >= 3) {
                            String[] ym = args[2].split("-");
                            year = Integer.parseInt(ym[0]);
                            month = Integer.parseInt(ym[1]);
                        } else {
                            LocalDate now = LocalDate.now();
                            year = now.getYear();
                            month = now.getMonthValue();
                        }
                        try {
                            java.lang.reflect.Method m = svc.getClass().getMethod("listBadgesForMonth", Long.class, int.class, int.class);
                            Object badgesObj = m.invoke(svc, patientId, year, month);
                            java.util.List<?> badges = badgesObj == null ? java.util.List.of() : (java.util.List<?>) badgesObj;
                            System.out.println("Badges for " + year + "-" + String.format("%02d", month) + " (patient=" + patientId + ") -> " + badges.size());
                            for (Object b : badges) System.out.println("  - " + formatBadgeReflect(b));
                        } catch (Throwable e) {
                            System.out.println("Exception while invoking SummaryService.listBadgesForMonth:");
                            e.printStackTrace(System.out);
                        }
                        break;
                    }

                    case "badge": {
                        if (args.length < 2) {
                            System.out.println("Usage: physio:summary badge <badgeId>");
                            return;
                        }
                        String badgeId = args[1];
                        try {
                            java.lang.reflect.Method m = svc.getClass().getMethod("getBadgeById", String.class);
                            Object b = m.invoke(svc, badgeId);
                            if (b == null) {
                                System.out.println("Badge not found: " + badgeId);
                                return;
                            }
                            System.out.println(formatBadgeReflect(b));
                            try {
                                java.lang.reflect.Method md = b.getClass().getMethod("getDescription");
                                Object desc = md.invoke(b);
                                System.out.println("  description: " + (desc == null ? "" : desc.toString()));
                            } catch (NoSuchMethodException ignore) {
                            }
                        } catch (Throwable e) {
                            System.out.println("Exception while invoking SummaryService.getBadgeById:");
                            e.printStackTrace(System.out);
                        }
                        break;
                    }

                    case "":
                        System.out.println("Usage: physio:summary <view|badges|badge> ...");
                        break;

                    default:
                        System.out.println("Unknown summary action: " + action);
                        break;
                }
            } finally {
                if (ref != null) ctx.ungetService(ref);
            }
        }

        public void progress(String action, String... args) {
            String a = (action == null) ? "" : action.trim().toLowerCase();

            switch (a) {
                case "patients" -> withService(UserManagementService.class, userSvc -> {
                    List<User> patients = userSvc.listByRole("PATIENT");

                    if (patients == null || patients.isEmpty()) {
                        System.out.println("No patients found.");
                        return;
                    }

                    System.out.println("\n[Patients]");
                    for (User patient : patients) {
                        System.out.println("ID   : " + patient.getId());
                        System.out.println("Name : " + patient.getUsername());
                        System.out.println("----------------------");
                    }           
                });

                // View Patient Details
                case "details" -> withService(UserManagementService.class, userSvc -> {
                    if (args.length < 1) {
                        System.out.println("Usage: physio:progress details <patientId>");
                        return;
                    }
                    Long patientId = parseLong(args[0], "patientId");
                    User patient = userSvc.findById(patientId);

                    if (patient == null) {
                        System.out.println("Patient not found.");
                        return;
                    }

                    System.out.println("\n[Patient Details Information]");
                    System.out.println("ID: " + patient.getId());
                    System.out.println("Username: " + patient.getUsername());
                    System.out.println("Email: " + patient.getEmail());
                    System.out.println("Phone: " + patient.getPhone());
                    System.out.println("Address: " + patient.getAddress());
                    System.out.println("Language Preference: " + patient.getLanguage());
                    System.out.println("Active: " + patient.isActive());                
                });

                // View Progress Reports
                case "reports" -> withService(ProgressTrackingService.class, progSvc -> {
                    if (args.length < 1) {
                        System.out.println("Usage: physio:progress reports <patientId>");
                        return;
                    }
                    Long patientId = parseLong(args[0], "patientId");

                    System.out.println("\n[Patient Progress Reports]");

                    List<TreatmentReport> reports = progSvc.getPatientReports(patientId);

                    if (reports == null || reports.isEmpty()) {
                        System.out.println("   -> No reports found for this patient.");
                        return;
                    }

                    for (TreatmentReport report : reports) {
                        System.out.println(
                                "ID: " + report.getId()
                                        + " | Title: " + report.getReportTitle()
                                        + " | Type: " + report.getReportType()
                                        + " | Activity: " + report.getActivity()
                                        + " | Performance: " + report.getPerformance()
                                        + " | Date: " +
                                        (report.getDateTime() != null ? report.getDateTime() : "(not set)")
                        );
                    }                
                });

                // Create Treatment Report
                case "create" -> withService(ProgressTrackingService.class, progSvc -> {
                    if (args.length < 5) {
                        System.out.println(
                                "Usage: physio:progress create <patientId> <title> <type> <activity> <performance>");
                        System.out.println(
                                "Example: physio:progress create 4 \"Week 1\" Rehab Stretching 85");
                        return;
                    }

                    Long patientId = parseLong(args[0], "patientId");
                    String title = args[1];
                    String type = args[2];
                    String activity = args[3];
                    int performance = Integer.parseInt(args[4]);

                    try {
                        TreatmentReport report = progSvc.createReport(title, type, performance, activity, patientId);
                        System.out.println("SUCCESS: Report created with ID " + report.getId());
                    } catch (Exception e) {
                        System.out.println("FAILED to create report: " + e.getMessage());
                    }
                });

                default -> {
                    System.out.println("Unknown progress command.");
                    System.out.println("Try:");
                    System.out.println("  physio:progress patients");
                    System.out.println("  physio:progress details <patientId>");
                    System.out.println("  physio:progress reports <patientId>");
                    System.out.println("  physio:progress create <patientId> <title> <type> <activity> <performance>");
                }
            }
        }


        // -----------------------------------------------------
        // MODULE 1: USER MANAGEMENT CLI
        // -----------------------------------------------------
        public void user(String action, String... args) {
            String a = (action == null) ? "" : action.trim().toLowerCase();

            withService(UserManagementService.class, svc -> {
                switch (a) {
                    case "create" -> {
                        if (args.length < 3) {
                            System.out.println("Usage: physio:user create <username> <email> <role> [clinic]");
                            return;
                        }
                        String username = args[0];
                        String email = args[1];
                        String role = args[2];
                        String clinic = (args.length > 3) ? joinFrom(args, 3) : null;
                        User u = svc.createUser(username, email, role, clinic);
                        System.out.println("Created User: " + formatUser(u));
                    }
                    case "list" -> {
                        List<User> users = svc.listAll();
                        System.out.println("--- All Users (" + users.size() + ") ---");
                        users.forEach(u -> System.out.println(formatUser(u)));
                    }
                    case "role" -> {
                        if (args.length < 1) {
                            System.out.println("Usage: physio:user role <role>");
                            return;
                        }
                        List<User> users = svc.listByRole(args[0]);
                        System.out.println("--- Users with Role " + args[0] + " ---");
                        users.forEach(u -> System.out.println(formatUser(u)));
                    }
                    case "assign" -> {
                        if (args.length < 2) {
                            System.out.println("Usage: physio:user assign <patientId> <physioId>");
                            return;
                        }
                        Long patientId = parseLong(args[0], "patientId");
                        Long physioId = parseLong(args[1], "physioId");
                        boolean success = svc.assignPatientToPhysio(patientId, physioId);
                        System.out.println(success ? "Success: Patient assigned." : "Failed: Invalid IDs or roles.");
                    }
                    case "patients" -> {
                        if (args.length < 1) {
                            System.out.println("Usage: physio:user patients <physioId>");
                            return;
                        }
                        Long physioId = parseLong(args[0], "physioId");
                        List<User> patients = svc.findPatientsByPhysioId(physioId);
                        System.out.println("--- Patients assigned to Physio " + physioId + " ---");
                        patients.forEach(u -> System.out.println(formatUser(u)));
                    }
                    case "deactivate" -> {
                        if (args.length < 1) {
                            System.out.println("Usage: physio:user deactivate <email>");
                            return;
                        }
                        boolean success = svc.deactivate(args[0]);
                        System.out.println(success ? "User deactivated." : "User not found.");
                    }
                    default -> System.out.println("Unknown command. Try: create, list, role, assign, patients, deactivate");
                }
            });
        }

        // -----------------------------------------------------
        // MODULE 2: PERSONAL INFO CLI
        // -----------------------------------------------------
        public void personal(String action, String... args) {
            String a = (action == null) ? "" : action.trim().toLowerCase();

            withService(PersonalInfoService.class, svc -> {
                switch (a) {
                    case "profile" -> {
                        if (args.length < 1) {
                            System.out.println("Usage: physio:personal profile <userId>");
                            return;
                        }
                        Long id = parseLong(args[0], "userId");
                        User u = svc.getProfile(id);
                        System.out.println(u != null ? formatUser(u) : "User not found.");
                    }
                    case "update" -> {
                        if (args.length < 3) {
                            System.out.println("Usage: physio:personal update <userId> <address> <phone>");
                            return;
                        }
                        Long id = parseLong(args[0], "userId");
                        String addr = args[1];
                        String phone = args[2];
                        boolean success = svc.updateProfile(id, addr, phone, null);
                        System.out.println(success ? "Profile updated." : "Update failed (user not found).");
                    }
                    case "lang" -> {
                        if (args.length < 2) {
                            System.out.println("Usage: physio:personal lang <userId> <langCode>");
                            return;
                        }
                        Long id = parseLong(args[0], "userId");
                        String lang = args[1];
                        boolean success = svc.changeLanguage(id, lang);
                        System.out.println(success ? "Language changed." : "Update failed.");
                    }
                    default -> System.out.println("Unknown command. Try: profile, update, lang");
                }
            });
        }

        public void test(String action, String... args) {
            String act = (action == null) ? "" : action.trim().toLowerCase();

            switch (act) {
                case "evaluate" -> withService(TestService.class, svc -> {
                    List<Question> questions = svc.getScrenningTestQuestions();
                    if (args.length < questions.size()) {
                        System.out.println("Usage: physio:test evaluate <answer1> <answer2> ... <answer" + questions.size() + ">");
                        return;
                    }
                    List<String> answers = java.util.Arrays.asList(args).subList(0, questions.size());
                    int score = svc.evaluate(answers);
                    System.out.println("Screening test completed. Score: " + score);
                });

                case "questionlist" -> withService(TestManageService.class, svc -> {
                    List<Question> questions = svc.getQuestionList();
                    System.out.println("Questions :");
                    for (int i = 0; i < questions.size(); i++) {
                        Question question = questions.get(i);
                        System.out.println(" " + (i+1) + ". " + question.getQuestionDesc());
                    }
                });

                case "add" -> withService(TestManageService.class, svc -> {
                    if (args.length < 3) {
                        System.out.println("Usage: physio:test add <desc> <category> <correctAnswer>");
                        return;
                    }
                    
                    Question question = svc.addQuestion(args[0], args[1], args[2]);
                    System.out.println("Question added successfully:");
                    System.out.println("ID       : " + question.getQuestionId());
                    System.out.println("Desc     : " + question.getQuestionDesc());
                    System.out.println("Category : " + question.getQuestionCat());
                    System.out.println("Answer   : " + question.getQuestionAns());
                });

                case "edit" -> withService(TestManageService.class, svc -> {
                    if (args.length < 4) {
                        System.out.println(
                            "Usage: physio:test edit <index> <desc|-> <category|-> <correctAnswer|->"
                        );
                        System.out.println("Use '-' to keep the current value.");
                        return;
                    }

                    try {
                        int index = Integer.parseInt(args[0]) - 1;
                        List<Question> questions = svc.getQuestionList();

                        if (index < 0 || index >= questions.size()) {
                            System.out.println("Invalid question index: " + (index + 1));
                            return;
                        }

                        Question q = questions.get(index);

                        // Only update fields if user does NOT enter "-"
                        if (!args[1].equals("-")) {
                            q.setQuestionDesc(args[1]);
                        }

                        if (!args[2].equals("-")) {
                            q.setQuestionCat(args[2]);
                        }

                        if (!args[3].equals("-")) {
                            q.setQuestionAns(args[3]);
                        }

                        Question questionEdited = svc.editQuestion(q);

                        System.out.println("Edited question at index " + (index + 1));
                        System.out.println("Description : " + questionEdited.getQuestionDesc());
                        System.out.println("Category    : " + questionEdited.getQuestionCat());
                        System.out.println("Answer      : " + questionEdited.getQuestionAns());

                    } catch (NumberFormatException e) {
                        System.out.println("Invalid index: " + args[0]);
                    }
                });

                case "remove" -> withService(TestManageService.class, svc -> {
                    if (args.length < 1) {
                        System.out.println("Usage: physio:test remove <index>");
                        return;
                    }
                    try {
                        int index = Integer.parseInt(args[0]) - 1;
                        List<Question> questions = svc.getQuestionList();
                        if (index < 0 || index >= questions.size()) {
                            System.out.println("Invalid question index: " + (index + 1));
                            return;
                        }
                        Question q = questions.get(index);
                        svc.removeQuestion(q);
                        System.out.println("Removed question at index " + (index + 1) + ": " + q.getQuestionDesc());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid index: " + args[0]);
                    }
                });

                default -> System.out.println("Unknown test action: evaluate|questionlist|add|edit|remove");
            }
        }


        // -------------------------
        // Helpers
        // -------------------------
        private String formatUser(User u) {
            if (u == null) return "null";
            return String.format("[ID:%d] %s (%s) | Role:%s | Active:%s | Lang:%s | PhysioID:%s | Addr:%s",
                    u.getId(), u.getUsername(), u.getEmail(), u.getRole(), u.isActive(),
                    u.getLanguage(), u.getAssignedPhysioId(), u.getAddress());
        }
        
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
                ex.printStackTrace(System.out);
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

        private String formatBadgeReflect(Object b) {
            if (b == null) return "(null)";
            try {
                java.lang.reflect.Method getId = b.getClass().getMethod("getId");
                java.lang.reflect.Method getName = b.getClass().getMethod("getName");
                java.lang.reflect.Method isEarned = null;
                java.lang.reflect.Method getEarnedAt = null;
                try { isEarned = b.getClass().getMethod("isEarned"); } catch (NoSuchMethodException ignored) {}
                try { getEarnedAt = b.getClass().getMethod("getEarnedAt"); } catch (NoSuchMethodException ignored) {}

                Object id = getId.invoke(b);
                Object name = getName.invoke(b);
                Object earned = isEarned == null ? null : isEarned.invoke(b);
                Object at = getEarnedAt == null ? null : getEarnedAt.invoke(b);

                return "id=" + id
                        + " name=\"" + (name == null ? "" : name.toString()) + "\""
                        + " earned=" + (earned == null ? "?" : earned.toString())
                        + (at == null ? "" : " at=" + at.toString());
            } catch (Exception e) {
                return b.toString();
            }
        }
    }
}
