package com.physiotrack.therapy.api.model;

import java.util.ArrayList;
import java.util.List;

public class PTProgram {
    private Long id;
    private Long patientId;
    private List<PTActivity> activities = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public List<PTActivity> getActivities() { return activities; }
    public void setActivities(List<PTActivity> activities) { this.activities = activities; }
}