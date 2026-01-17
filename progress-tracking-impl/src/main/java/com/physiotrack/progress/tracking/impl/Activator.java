package com.physiotrack.progress.tracking.impl;

import com.physiotrack.progress.tracking.api.ProgressTrackingService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<ProgressTrackingService> reg;

    @Override
    public void start(BundleContext context) {
        reg = context.registerService(ProgressTrackingService.class, new ProgressTrackingServiceImpl(), null);
        System.out.println("[progress-tracking-impl] ProgressTrackingService registered");
    }

    @Override
    public void stop(BundleContext context) {
        if (reg != null) reg.unregister();
        System.out.println("[progress-tracking-impl] stopped");
    }
}
