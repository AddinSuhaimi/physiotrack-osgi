package com.physiotrack.summary.impl;

import com.physiotrack.summary.api.SummaryService;
import com.physiotrack.summary.api.model.Badge;
import com.physiotrack.summary.api.model.MonthlyProgress;
import com.physiotrack.summary.impl.repository.SummaryRepository;

import java.util.List;

public class SummaryServiceImpl implements SummaryService {

    private final SummaryRepository repo;

    public SummaryServiceImpl() {
        this(new SummaryRepository());
    }
    public SummaryServiceImpl(SummaryRepository repo) {
        if (repo == null) throw new IllegalArgumentException("repo required");
        this.repo = repo;
    }
    @Override
    public String ping() {
        return "summary module OK (months=" + repo.countMonthlyProgress() + ")";
    }
    @Override
    public MonthlyProgress getMonthlyProgress(Long patientId, int year, int month) {
        validateMonth(month);
        validateYear(year);
        return repo.getOrCreateMonthlyProgress(patientId, year, month);
    }
    @Override
    public List<Badge> listBadgesForMonth(Long patientId, int year, int month) {
        validateMonth(month);
        validateYear(year);
        return repo.getOrCreateBadgesForMonth(patientId, year, month);
    }
    @Override
    public Badge getBadgeById(String badgeId) {
        return repo.findBadgeById(badgeId);
    }
    private void validateMonth(int month) {
        if (month < 1 || month > 12) throw new IllegalArgumentException("month must be 1-12");
    }
    private void validateYear(int year) {
        if (year < 1) throw new IllegalArgumentException("year must be >= 1");
    }
}
