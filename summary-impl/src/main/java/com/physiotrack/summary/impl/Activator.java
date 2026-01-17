package com.physiotrack.summary.impl;

import com.physiotrack.summary.api.SummaryService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<SummaryService> reg;

    @Override
    public void start(BundleContext context) {
        reg = context.registerService(SummaryService.class, new SummaryServiceImpl(), null);
        System.out.println("[summary-impl] SummaryService registered");
    }

    @Override
    public void stop(BundleContext context) {
        if (reg != null) reg.unregister();
        System.out.println("[summary-impl] stopped");
    }
}
