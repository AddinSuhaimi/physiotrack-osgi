package com.physiotrack.therapy.api.model;

import java.util.ArrayList;
import java.util.List;

public class OTProgram {
    private Long id;
    private Long patientId;
    private List<OTActivity> activities = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public List<OTActivity> getActivities() { return activities; }
    public void setActivities(List<OTActivity> activities) { this.activities = activities; }
}