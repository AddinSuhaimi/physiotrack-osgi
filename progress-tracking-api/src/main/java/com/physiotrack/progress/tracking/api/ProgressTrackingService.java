package com.physiotrack.progress.tracking.api;

import java.util.List;
import java.util.Optional;

import com.physiotrack.progress.tracking.api.model.TreatmentReport;

public interface ProgressTrackingService {
    String ping();

    TreatmentReport createReport(String title, String type, int performance, String activity, Long patientId);

    List<TreatmentReport> getPatientReports(Long patientId);
    
}
