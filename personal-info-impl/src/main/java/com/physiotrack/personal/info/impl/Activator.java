package com.physiotrack.personal.info.impl;

import com.physiotrack.personal.info.api.PersonalInfoService;
import com.physiotrack.user.management.api.UserManagementService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<PersonalInfoService> registration;

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("[personal-info-impl] Starting...");

        // 1. Look up the UserManagementService
        ServiceReference<UserManagementService> ref = context.getServiceReference(UserManagementService.class);
        
        if (ref != null) {
            UserManagementService userMgr = context.getService(ref);

            // 2. Create our service, injecting the dependency
            PersonalInfoService personalInfoService = new PersonalInfoServiceImpl(userMgr);

            // 3. Register our service
            registration = context.registerService(PersonalInfoService.class, personalInfoService, null);
            System.out.println("[personal-info-impl] PersonalInfoService registered");
        } else {
            System.err.println("[personal-info-impl] FAILED: UserManagementService not found!");
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (registration != null) {
            registration.unregister();
        }
        System.out.println("[personal-info-impl] Stopped");
    }
}
