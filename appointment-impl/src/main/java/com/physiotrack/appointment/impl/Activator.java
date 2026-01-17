package com.physiotrack.appointment.impl;

import com.physiotrack.appointment.api.AppointmentService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<AppointmentService> reg;

    @Override
    public void start(BundleContext context) {
        reg = context.registerService(AppointmentService.class, new AppointmentServiceImpl(), null);
        System.out.println("[appointment-impl] AppointmentService registered");
    }

    @Override
    public void stop(BundleContext context) {
        if (reg != null) reg.unregister();
        System.out.println("[appointment-impl] stopped");
    }
}
