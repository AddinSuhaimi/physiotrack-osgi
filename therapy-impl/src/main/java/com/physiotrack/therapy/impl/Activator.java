package com.physiotrack.therapy.impl;

import com.physiotrack.therapy.api.TherapyService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<TherapyService> reg;

    @Override
    public void start(BundleContext context) {
        reg = context.registerService(TherapyService.class, new TherapyServiceImpl(), null);
        System.out.println("[therapy-impl] TherapyService registered");
    }

    @Override
    public void stop(BundleContext context) {
        if (reg != null) reg.unregister();
        System.out.println("[therapy-impl] stopped");
    }
}
