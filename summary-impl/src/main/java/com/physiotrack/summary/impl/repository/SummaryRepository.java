package com.physiotrack.summary.impl.repository;

import com.physiotrack.summary.api.model.Badge;
import com.physiotrack.summary.api.model.MonthlyProgress;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SummaryRepository {

    private final ConcurrentHashMap<String, MonthlyProgress> progressByKey = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<Badge>> badgesByKey = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Badge> badgeById = new ConcurrentHashMap<>();

    public long countMonthlyProgress() {
        return progressByKey.size();
    }
    public MonthlyProgress findMonthlyProgress(Long patientId, int year, int month) {
        return progressByKey.get(progressKey(patientId, year, month));
    }
    public MonthlyProgress getOrCreateMonthlyProgress(Long patientId, int year, int month) {
        String key = progressKey(patientId, year, month);
        return progressByKey.computeIfAbsent(key, ignored -> generateMonthlyProgress(patientId, year, month));
    }
    public List<Badge> findBadgesForMonth(Long patientId, int year, int month) {
        return badgesByKey.get(badgesKey(patientId, year, month));
    }
    public List<Badge> getOrCreateBadgesForMonth(Long patientId, int year, int month) {
        String key = badgesKey(patientId, year, month);
        return badgesByKey.computeIfAbsent(key, ignored -> {
            List<Badge> badges = generateBadgesForMonth(patientId, year, month);
            // Index badges by id only for the deterministic "default patient" (0L).
            // This keeps getBadgeById(...) stable even though earned/earnedAt are patient-dependent in the MVP model.
            if (patientId != null && patientId == 0L) {
                for (Badge b : badges) {
                    if (b != null && b.getId() != null) badgeById.putIfAbsent(b.getId(), b);
                }
            }
            return badges;
        });
    }
    public Badge findBadgeById(String badgeId) {
        if (badgeId == null) return null;
        Badge direct = badgeById.get(badgeId);
        if (direct != null) return direct;

        // Lazy materialize from id pattern: B-year-month-n (patient not encoded)
        String[] parts = badgeId.split("-");
        if (parts.length < 4) return null;
        try {
            int year = Integer.parseInt(parts[1]);
            int month = Integer.parseInt(parts[2]);

            // Use a deterministic "default patient" for materialization (matches prior behavior)
            getOrCreateBadgesForMonth(0L, year, month);
            return badgeById.get(badgeId);
        } catch (Exception e) {
            return null;
        }
    }
    private MonthlyProgress generateMonthlyProgress(Long patientId, int year, int month) {
        MonthlyProgress p = new MonthlyProgress();
        p.setYear(year);
        p.setMonth(month);

        int total = 12;
        long pid = patientId == null ? 0L : patientId;
        int completed = 8 + (int) (Math.abs(pid) % 3);
        double rate = total == 0 ? 0.0 : (completed * 100.0 / total);

        p.setTotalSessions(total);
        p.setCompletedSessions(completed);
        p.setCompletionRate(Math.round(rate * 100.0) / 100.0);

        p.setBadges(getOrCreateBadgesForMonth(patientId, year, month));
        return p;
    }
    private List<Badge> generateBadgesForMonth(Long patientId, int year, int month) {
        List<Badge> out = new ArrayList<>();

        long pid = patientId == null ? 0L : patientId;

        Badge b1 = new Badge();
        b1.setId("B-" + year + "-" + month + "-1");
        b1.setName("Consistent Performer");
        b1.setDescription("Completed at least 75% of sessions this month.");
        b1.setEarned(pid % 2 == 0);
        b1.setEarnedAt(b1.isEarned() ? LocalDate.of(year, month, 5) : null);

        Badge b2 = new Badge();
        b2.setId("B-" + year + "-" + month + "-2");
        b2.setName("Early Bird");
        b2.setDescription("Attended a session before 8:00 AM at least once.");
        b2.setEarned(pid % 3 == 0);
        b2.setEarnedAt(b2.isEarned() ? LocalDate.of(year, month, 3) : null);

        out.add(b1);
        out.add(b2);

        return out;
    }
    private String progressKey(Long patientId, int year, int month) {
        return (patientId == null ? "null" : patientId.toString()) + ":" + year + ":" + month;
    }
    private String badgesKey(Long patientId, int year, int month) {
        return (patientId == null ? "null" : patientId.toString()) + ":" + year + ":" + month;
    }
}
