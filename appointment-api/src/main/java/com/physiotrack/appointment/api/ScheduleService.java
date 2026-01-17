package com.physiotrack.appointment.api;

import com.physiotrack.appointment.api.model.Appointment;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {
    String ping();
    List<Appointment> getScheduleForPhysio(Long physioId, LocalDateTime from, LocalDateTime to);
    boolean isSlotAvailable(Long physioId, LocalDateTime dateTime);
}
