package com.physiotrack.progress.tracking.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.physiotrack.progress.tracking.api.ProgressTrackingService;
import com.physiotrack.progress.tracking.api.model.TreatmentReport;
import com.physiotrack.progress.tracking.impl.repository.TreatmentReportRepository;

public class ProgressTrackingServiceImpl implements ProgressTrackingService {

    public final TreatmentReportRepository treatmentReportRepository;

    public ProgressTrackingServiceImpl(
        TreatmentReportRepository treatmentReportRepository
    ) {
        this.treatmentReportRepository = treatmentReportRepository;
    }


    @Override
    public String ping() {
        return "progress-tracking module OK";
    }

    @Override
    public TreatmentReport createReport(String title, String type, int performance, String activity, Long patientId) {
        TreatmentReport report = new TreatmentReport();
        report.setReportTitle(title);
        report.setReportType(type);
        report.setActivity(activity);
        report.setPerformance(performance);
        report.setDateTime(LocalDateTime.now());
        report.setPatientId(patientId);

        treatmentReportRepository.save(report);
        return report;
    };

    @Override
    public List<TreatmentReport> getPatientReports(Long patientId) {
        return treatmentReportRepository.findAll().stream()
                .filter(r -> r.getPatientId() != null)
                .filter(r -> r.getPatientId().equals(patientId))
                .toList();
    }

}
