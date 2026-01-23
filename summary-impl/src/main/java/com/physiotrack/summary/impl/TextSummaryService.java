package com.physiotrack.summary.impl;

public class TextSummaryService implements com.physiotrack.summary.text.TextSummaryService {

    private final SummaryServiceImpl delegate;

    public TextSummaryService() {
        this(new SummaryServiceImpl());
    }

    public TextSummaryService(SummaryServiceImpl delegate) {
        if (delegate == null) throw new IllegalArgumentException("delegate required");
        this.delegate = delegate;
    }

    @Override
    public String getMonthlySummaryText(Long patientId, int year, int month) {
        try {
            com.physiotrack.summary.api.model.MonthlyProgress p = delegate.getMonthlyProgress(patientId, year, month);
            if (p == null) return "(no summary)";
            StringBuilder sb = new StringBuilder();
            sb.append("Monthly Summary for patient=").append(patientId).append(" ")
                    .append(p.getYear()).append("-").append(String.format("%02d", p.getMonth())).append("\n");
            sb.append(" totalSessions=").append(p.getTotalSessions())
                    .append(" completed=").append(p.getCompletedSessions())
                    .append(" completionRate=").append(p.getCompletionRate()).append("%\n");
            if (p.getBadges() != null && !p.getBadges().isEmpty()) {
                sb.append(" badges:\n");
                for (com.physiotrack.summary.api.model.Badge b : p.getBadges()) {
                    sb.append("  - id=").append(b.getId())
                            .append(" name=\"").append(b.getName()).append("\" earned=").append(b.isEarned())
                            .append(b.getEarnedAt() != null ? " at=" + b.getEarnedAt() : "").append("\n");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "(error building summary: " + e.getMessage() + ")";
        }
    }
}
