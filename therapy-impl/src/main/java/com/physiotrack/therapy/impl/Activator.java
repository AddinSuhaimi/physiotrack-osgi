
package com.physiotrack.therapy.impl;

import com.physiotrack.therapy.api.TherapyManagementService;
import com.physiotrack.therapy.api.TherapyProgressService;
import com.physiotrack.therapy.api.model.PTProgram;
import com.physiotrack.therapy.api.model.PTActivity;
import com.physiotrack.user.management.api.UserManagementService;
import com.physiotrack.user.management.api.model.User;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<TherapyManagementService> regMgmt;
    private ServiceRegistration<TherapyProgressService> regProgress;

    @Override

    public void start(BundleContext context) {
        TherapyManagementServiceImpl mgmtService = new TherapyManagementServiceImpl();
        regMgmt = context.registerService(TherapyManagementService.class, mgmtService, null);
        TherapyProgressServiceImpl progressService = new TherapyProgressServiceImpl(mgmtService);
        regProgress = context.registerService(TherapyProgressService.class, progressService, null);
        System.out.println("[therapy-impl] TherapyManagementService and TherapyProgressService registered");
    }

    @Override
    public void stop(BundleContext context) {
        if (regMgmt != null) regMgmt.unregister();
        if (regProgress != null) regProgress.unregister();
        System.out.println("[therapy-impl] stopped");
    }
}
