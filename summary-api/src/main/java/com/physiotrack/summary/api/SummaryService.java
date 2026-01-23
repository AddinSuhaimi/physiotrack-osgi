package com.physiotrack.summary.api;

import com.physiotrack.summary.api.model.Badge;
import com.physiotrack.summary.api.model.MonthlyProgress;

import java.util.List;

public interface SummaryService {
    String ping();
    MonthlyProgress getMonthlyProgress(Long patientId, int year, int month);
    List<Badge> listBadgesForMonth(Long patientId, int year, int month);
    Badge getBadgeById(String badgeId);
}
