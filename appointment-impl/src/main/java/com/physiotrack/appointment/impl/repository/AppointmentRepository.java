package com.physiotrack.appointment.impl.repository;

import com.physiotrack.appointment.api.model.Appointment;
import com.physiotrack.appointment.api.model.AppointmentRequestType;
import com.physiotrack.appointment.api.model.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class AppointmentRepository {

    private final ConcurrentHashMap<Long, Appointment> byId = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public long count() {
        return byId.size();
    }

    public Appointment save(Appointment a) {
        if (a.getId() == null) {
            a.setId(seq.getAndIncrement());
        }
        byId.put(a.getId(), a);
        return a;
    }

    public Appointment findById(Long id) {
        if (id == null) return null;
        return byId.get(id);
    }

    public List<Appointment> findByPatientId(Long patientId) {
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : byId.values()) {
            if (patientId != null && patientId.equals(a.getPatientId())) {
                out.add(a);
            }
        }
        out.sort(Comparator.comparing(Appointment::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return out;
    }

    public List<Appointment> findPendingByType(AppointmentRequestType type) {
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : byId.values()) {
            if (a.getRequestType() == type && a.getStatus() == AppointmentStatus.PENDING) {
                out.add(a);
            }
        }
        out.sort(Comparator.comparing(Appointment::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return out;
    }

    public boolean existsApprovedSlot(Long physioId, LocalDateTime dateTime) {
        for (Appointment a : byId.values()) {
            if (physioId != null
                    && physioId.equals(a.getPhysioId())
                    && a.getStatus() == AppointmentStatus.APPROVED
                    && dateTime != null
                    && dateTime.equals(a.getDateTime())) {
                return true;
            }
        }
        return false;
    }

    public List<Appointment> findApprovedForPhysioBetween(Long physioId, LocalDateTime from, LocalDateTime to) {
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : byId.values()) {
            if (physioId != null
                    && physioId.equals(a.getPhysioId())
                    && a.getStatus() == AppointmentStatus.APPROVED
                    && a.getDateTime() != null
                    && !a.getDateTime().isBefore(from)
                    && !a.getDateTime().isAfter(to)) {
                out.add(a);
            }
        }
        out.sort(Comparator.comparing(Appointment::getDateTime, Comparator.nullsLast(Comparator.naturalOrder())));
        return out;
    }
}
