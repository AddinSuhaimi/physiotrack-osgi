package com.physiotrack.test.impl;

import com.physiotrack.test.api.TestService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<TestService> reg;

    @Override
    public void start(BundleContext context) {
        reg = context.registerService(TestService.class, new TestServiceImpl(), null);
        System.out.println("[test-impl] TestService registered");
    }

    @Override
    public void stop(BundleContext context) {
        if (reg != null) reg.unregister();
        System.out.println("[test-impl] stopped");
    }
}
