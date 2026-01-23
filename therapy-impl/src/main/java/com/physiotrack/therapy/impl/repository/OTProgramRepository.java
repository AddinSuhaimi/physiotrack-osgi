package com.physiotrack.therapy.impl.repository;

import com.physiotrack.therapy.api.model.OTProgram;

public interface OTProgramRepository extends TherapyProgramRepository<OTProgram> {
    OTProgram findByPatientId(Long patientId);
    OTProgram findById(Long id);
    void save(OTProgram program);
    long count();
}