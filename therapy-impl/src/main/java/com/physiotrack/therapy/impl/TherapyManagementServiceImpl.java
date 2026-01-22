package com.physiotrack.therapy.impl;

import com.physiotrack.therapy.api.TherapyManagementService;
import com.physiotrack.therapy.api.model.*;

import java.util.*;

public class TherapyManagementServiceImpl implements TherapyManagementService {
    // In-memory storage for demo
    private final Map<Long, PTProgram> ptPrograms = new HashMap<>();
    private final Map<Long, OTProgram> otPrograms = new HashMap<>();
    private static long activityCounter = 1;

    @Override
    public void saveOTProgram(OTProgram program) {
        otPrograms.put(program.getPatientId(), program);
    }

    @Override
    public PTProgram findPTProgramByPatientId(Long patientId) {
        return ptPrograms.get(patientId);
    }

    @Override
    public void savePTProgram(PTProgram program) {
        // For demo, use patientId as key
        ptPrograms.put(program.getPatientId(), program);
    }

    @Override
    public OTProgram findOTProgramByPatientId(Long patientId) {
        return otPrograms.get(patientId);
    }

    @Override
    public void addPTActivity(Long patientId, PTActivity activity) {
        PTProgram program = ptPrograms.get(patientId);
        if (program != null) {
            activity.setId(activityCounter++);
            program.getActivities().add(activity);
        }
    }

    @Override
    public void removePTActivity(Long patientId, Long activityId) {
        PTProgram program = ptPrograms.get(patientId);
        if (program != null) {
            program.getActivities().removeIf(a -> Objects.equals(a.getId(), activityId));
        }
    }

    @Override
    public void addOTActivity(Long patientId, OTActivity activity) {
        OTProgram program = otPrograms.get(patientId);
        if (program != null) {
            activity.setId(activityCounter++);
            program.getActivities().add(activity);
        }
    }

    @Override
    public void removeOTActivity(Long patientId, Long activityId) {
        OTProgram program = otPrograms.get(patientId);
        if (program != null) {
            program.getActivities().removeIf(a -> Objects.equals(a.getId(), activityId));
        }
    }
}