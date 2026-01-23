package com.physiotrack.therapy.impl;

import com.physiotrack.therapy.api.TherapyProgressService;
import com.physiotrack.therapy.api.TherapyManagementService;
import com.physiotrack.therapy.api.model.*;


import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TherapyProgressServiceImpl implements TherapyProgressService {
    private final TherapyManagementService managementService;

    public TherapyProgressServiceImpl(TherapyManagementService managementService) {
        this.managementService = managementService;
    }

    @Override
    public List<PTActivity> getPTActivities(Long programId) {
        PTProgram program = managementService.findPTProgramByPatientId(programId);
        return program != null ? program.getActivities() : Collections.emptyList();
    }

    @Override
    public void markPTCompleted(Long programId, Long activityId) {
        PTProgram program = managementService.findPTProgramByPatientId(programId);
        if (program != null) {
            program.getActivities().stream()
                .filter(a -> Objects.equals(a.getId(), activityId))
                .findFirst()
                .ifPresent(a -> a.setCompleted(true));
        }
    }

    @Override
    public List<OTActivity> getOTActivities(Long programId) {
        OTProgram program = managementService.findOTProgramByPatientId(programId);
        return program != null ? program.getActivities() : Collections.emptyList();
    }

    @Override
    public void markOTCompleted(Long programId, Long activityId) {
        OTProgram program = managementService.findOTProgramByPatientId(programId);
        if (program != null) {
            program.getActivities().stream()
                .filter(a -> Objects.equals(a.getId(), activityId))
                .findFirst()
                .ifPresent(a -> a.setCompleted(true));
        }
    }
}