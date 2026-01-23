package com.physiotrack.progress.tracking.impl.repository;

import com.physiotrack.progress.tracking.api.model.TreatmentReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TreatmentReportRepository {

    private final ConcurrentHashMap<Long, TreatmentReport> treatmentReports = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public long count() {
        return treatmentReports.size();
    }

    public TreatmentReport save(TreatmentReport treatmentReport) {
        if (treatmentReport.getId() == null) {
            treatmentReport.setId(seq.getAndIncrement());
        }
        treatmentReports.put(treatmentReport.getId(), treatmentReport);
        return treatmentReport;
    }

    public Optional<TreatmentReport> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(treatmentReports.get(id));
    }

    public Optional<TreatmentReport> findByType(String type) {
        if (type == null) return Optional.empty();

        for (TreatmentReport treatmentReport : treatmentReports.values()) {
            if (type.equals(treatmentReport.getReportType())) {
                return Optional.of(treatmentReport);
            }
        }
        return Optional.empty();
    }

    public List<TreatmentReport> findAll() {
        return new ArrayList<>(treatmentReports.values());
    }
}
