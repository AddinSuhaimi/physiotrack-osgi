package com.physiotrack.journal.impl;

import com.physiotrack.journal.api.JournalService;
import com.physiotrack.journal.api.model.Journal;
import com.physiotrack.journal.impl.repository.JournalRepository;

import java.time.LocalDateTime;
import java.util.List;

public class JournalServiceImpl implements JournalService {

    private final JournalRepository repo = new JournalRepository();

    @Override
    public String ping() {
        return "journal module OK (entries=" + repo.count() + ")";
    }

    @Override
    public Journal createJournal(Long patientId, Journal journal) {
        if (journal == null) throw new IllegalArgumentException("journal required");
        journal.setPatientId(patientId);
        LocalDateTime now = LocalDateTime.now();
        journal.setCreatedAt(now);
        journal.setUpdatedAt(now);
        return repo.save(journal);
    }

    @Override
    public Journal updateJournal(Long patientId, Long journalId, Journal updated) {
        Journal existing = repo.findById(journalId);
        if (existing == null) throw new IllegalArgumentException("Journal not found: " + journalId);
        if (patientId != null && !patientId.equals(existing.getPatientId())) {
            throw new IllegalArgumentException("Permission denied");
        }

        // patch fields if provided
        if (updated.getTitle() != null) existing.setTitle(updated.getTitle());
        if (updated.getWeather() != null) existing.setWeather(updated.getWeather());
        if (updated.getFeeling() != null) existing.setFeeling(updated.getFeeling());
        if (updated.getHealthCondition() != null) existing.setHealthCondition(updated.getHealthCondition());
        if (updated.getComment() != null) existing.setComment(updated.getComment());
        if (updated.getImageUrl() != null) existing.setImageUrl(updated.getImageUrl());

        existing.setUpdatedAt(LocalDateTime.now());
        return repo.save(existing);
    }

    @Override
    public boolean deleteJournal(Long patientId, Long journalId) {
        Journal existing = repo.findById(journalId);
        if (existing == null) return false;
        if (patientId != null && !patientId.equals(existing.getPatientId())) return false;
        return repo.deleteById(journalId);
    }

    @Override
    public Journal getJournalById(Long journalId) {
        return repo.findById(journalId);
    }

    @Override
    public List<Journal> listJournalsForPatient(Long patientId) {
        return repo.findByPatientId(patientId);
    }

    @Override
    public Journal setSharePermission(Long journalId, boolean share) {
        Journal existing = repo.findById(journalId);
        if (existing == null) throw new IllegalArgumentException("Journal not found: " + journalId);
        existing.setSharedWithPhysio(share);
        existing.setUpdatedAt(LocalDateTime.now());
        return repo.save(existing);
    }
}

