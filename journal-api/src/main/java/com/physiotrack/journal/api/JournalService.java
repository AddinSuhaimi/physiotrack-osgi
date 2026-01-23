package com.physiotrack.journal.api;

import com.physiotrack.journal.api.model.Journal;
import java.util.List;

public interface JournalService {
    String ping();
    Journal createJournal(Long patientId, Journal journal);
    Journal updateJournal(Long patientId, Long journalId, Journal updated);
    boolean deleteJournal(Long patientId, Long journalId);
    Journal getJournalById(Long journalId);
    List<Journal> listJournalsForPatient(Long patientId);
    Journal setSharePermission(Long journalId, boolean share);
}
