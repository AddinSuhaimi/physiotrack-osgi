package com.physiotrack.therapy.impl.repository;

import com.physiotrack.therapy.api.model.PTProgram;

public interface PTProgramRepository extends TherapyProgramRepository<PTProgram> {
    PTProgram findByPatientId(Long patientId);
    PTProgram findById(Long id);
    void save(PTProgram program);
    long count();
}