package com.physiotrack.journal.impl;

import com.physiotrack.journal.api.JournalService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<JournalService> reg;

    @Override
    public void start(BundleContext context) {
        reg = context.registerService(JournalService.class, new JournalServiceImpl(), null);
        System.out.println("[journal-impl] JournalService registered");
    }

    @Override
    public void stop(BundleContext context) {
        if (reg != null) reg.unregister();
        System.out.println("[journal-impl] stopped");
    }
}
