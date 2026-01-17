package com.physiotrack.appointment.impl;

import com.physiotrack.appointment.api.AppointmentService;
import org.osgi.service.component.annotations.Component;

@Component(service = AppointmentService.class)
public class AppointmentServiceImpl implements AppointmentService {

    @Override
    public String ping() {
        return "appointment service OK";
    }
}
