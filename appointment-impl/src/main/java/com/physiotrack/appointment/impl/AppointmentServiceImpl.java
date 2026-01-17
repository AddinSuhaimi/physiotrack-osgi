package com.physiotrack.appointment.impl;

import com.physiotrack.appointment.api.AppointmentService;

public class AppointmentServiceImpl implements AppointmentService {
    @Override
    public String ping() {
        return "appointment module OK";
    }
}
