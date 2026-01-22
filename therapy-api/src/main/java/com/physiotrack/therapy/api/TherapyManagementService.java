package com.physiotrack.therapy.api;

import com.physiotrack.therapy.api.model.PTActivity;
import com.physiotrack.therapy.api.model.PTProgram;
import com.physiotrack.therapy.api.model.OTActivity;
import com.physiotrack.therapy.api.model.OTProgram;

public interface TherapyManagementService {
    PTProgram findPTProgramByPatientId(Long patientId);
    OTProgram findOTProgramByPatientId(Long patientId);

    PTProgram getPTProgramById(Long programId);
    void savePTProgram(PTProgram program);

    OTProgram getOTProgramById(Long programId);
    void saveOTProgram(OTProgram program);

    void addPTActivity(Long programId, PTActivity activity);
    void removePTActivity(Long programId, Long activityId);

    void addOTActivity(Long programId, OTActivity activity);
    void removeOTActivity(Long programId, Long activityId);
}