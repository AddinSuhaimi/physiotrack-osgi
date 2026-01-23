package com.physiotrack.summary.text;

public interface TextSummaryService {
    String getMonthlySummaryText(Long patientId, int year, int month);
}

