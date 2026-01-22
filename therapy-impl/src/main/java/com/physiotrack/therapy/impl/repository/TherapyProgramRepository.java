package com.physiotrack.therapy.impl.repository;

public interface TherapyProgramRepository<T> {
    T findById(Long id);
    T findByPatientId(Long patientId);
    void save(T program);
    long count();
}