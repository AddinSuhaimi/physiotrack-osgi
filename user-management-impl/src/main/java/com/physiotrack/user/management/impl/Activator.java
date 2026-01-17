package com.physiotrack.user.management.impl;

import com.physiotrack.user.management.api.UserManagementService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<UserManagementService> reg;

    @Override
    public void start(BundleContext context) {
        reg = context.registerService(UserManagementService.class, new UserManagementServiceImpl(), null);
        System.out.println("[user-management-impl] UserManagementService registered");
    }

    @Override
    public void stop(BundleContext context) {
        if (reg != null) reg.unregister();
        System.out.println("[user-management-impl] stopped");
    }
}
