package com.physiotrack.journal.impl.repository;

import com.physiotrack.journal.api.model.Journal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class JournalRepository {

    private final ConcurrentHashMap<Long, Journal> byId = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public long count() { return byId.size(); }

    public Journal save(Journal j) {
        if (j.getId() == null) {
            j.setId(seq.getAndIncrement());
        }
        byId.put(j.getId(), j);
        return j;
    }

    public Journal findById(Long id) {
        if (id == null) return null;
        return byId.get(id);
    }

    public List<Journal> findByPatientId(Long patientId) {
        List<Journal> out = new ArrayList<>();
        for (Journal j : byId.values()) {
            if (patientId != null && patientId.equals(j.getPatientId())) {
                out.add(j);
            }
        }
        out.sort(Comparator.comparing(Journal::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return out;
    }

    public boolean deleteById(Long id) {
        return byId.remove(id) != null;
    }
}
