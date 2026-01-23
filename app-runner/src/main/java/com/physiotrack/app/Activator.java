package com.physiotrack.app;

import com.physiotrack.appointment.api.AppointmentService;
import com.physiotrack.appointment.api.ScheduleService;
import com.physiotrack.appointment.api.model.Appointment;
import com.physiotrack.journal.api.JournalService;
import com.physiotrack.journal.api.model.Journal;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

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
            System.out.println("  physio:journal <create|view|list|edit|delete|share>");
            System.out.println("  physio:summary <view|badges|badge>");
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

        public void therapy(String... args) {
            System.out.println("[TODO] Therapy module CLI not implemented yet.");
        }

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
