package com.physiotrack.progress.tracking.api.model;

import java.time.LocalDateTime;

public class TreatmentReport {

    private Long id;

    private Long patientId;
    private String reportTitle;
    private LocalDateTime dateTime;
    private String reportType;
    private int performance;
    private String activity;

    // Constructors
    public TreatmentReport() {}

    public TreatmentReport(Long patientId,String reportTitle, LocalDateTime dateTime, String reportType, int performance, String activity) {
        this.patientId=patientId;
        this.reportTitle = reportTitle;
        this.dateTime = dateTime;
        this.reportType = reportType;
        this.performance = performance;
        this.activity = activity;
    }

    // Getters and Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }   
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public int getPerformance() {
        return performance;
    }

    public void setPerformance(int performance) {
        this.performance = performance;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
