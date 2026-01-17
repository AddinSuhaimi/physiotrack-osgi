package com.physiotrack.personal.info.impl;

import com.physiotrack.personal.info.api.PersonalInfoService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<PersonalInfoService> reg;

    @Override
    public void start(BundleContext context) {
        reg = context.registerService(PersonalInfoService.class, new PersonalInfoServiceImpl(), null);
        System.out.println("[personal-info-impl] PersonalInfoService registered");
    }

    @Override
    public void stop(BundleContext context) {
        if (reg != null) reg.unregister();
        System.out.println("[personal-info-impl] stopped");
    }
}
