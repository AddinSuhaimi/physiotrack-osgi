package com.physiotrack.appointment.impl;

import com.physiotrack.appointment.api.ScheduleService;
import com.physiotrack.appointment.api.model.Appointment;
import com.physiotrack.appointment.impl.repository.AppointmentRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ScheduleServiceImpl implements ScheduleService {

    private final AppointmentRepository repo;

    public ScheduleServiceImpl(AppointmentRepository repo) {
        this.repo = repo;
    }

    @Override
    public String ping() {
        return "schedule OK";
    }

    @Override
    public List<Appointment> getScheduleForPhysio(Long physioId, LocalDateTime from, LocalDateTime to) {
        return repo.findApprovedForPhysioBetween(physioId, from, to);
    }

    @Override
    public boolean isSlotAvailable(Long physioId, LocalDateTime dateTime) {
        return !repo.existsApprovedSlot(physioId, dateTime);
    }
}
