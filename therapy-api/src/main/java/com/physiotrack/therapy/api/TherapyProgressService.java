package com.physiotrack.therapy.api;

import com.physiotrack.therapy.api.model.PTActivity;
import com.physiotrack.therapy.api.model.OTActivity;

import java.util.List;

public interface TherapyProgressService {
    List<PTActivity> getPTActivities(Long programId);
    void markPTCompleted(Long programId, Long activityId);

    List<OTActivity> getOTActivities(Long programId);
    void markOTCompleted(Long programId, Long activityId);
}