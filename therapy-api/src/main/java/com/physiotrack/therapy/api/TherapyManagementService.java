package com.physiotrack.therapy.api;

import com.physiotrack.therapy.api.model.PTActivity;
import com.physiotrack.therapy.api.model.PTProgram;
import com.physiotrack.therapy.api.model.OTActivity;
import com.physiotrack.therapy.api.model.OTProgram;

public interface TherapyManagementService {
    PTProgram findPTProgramByPatientId(Long patientId);
    OTProgram findOTProgramByPatientId(Long patientId);

    void savePTProgram(PTProgram program);
    void saveOTProgram(OTProgram program);

    void addPTActivity(Long patientId, PTActivity activity);
    void removePTActivity(Long patientId, Long activityId);

    void addOTActivity(Long patientId, OTActivity activity);
    void removeOTActivity(Long patientId, Long activityId);
}