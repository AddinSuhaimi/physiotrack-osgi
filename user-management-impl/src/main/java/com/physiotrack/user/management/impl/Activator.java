package com.physiotrack.user.management.impl;

import com.physiotrack.user.management.api.UserManagementService;
import com.physiotrack.user.management.impl.repository.UserRepository;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<UserManagementService> reg;

    @Override
    public void start(BundleContext context) {
        UserRepository repo = new UserRepository();
        UserManagementService svc = new UserManagementServiceImpl(repo);

        // Seed demo users
        seedUsers((UserManagementServiceImpl) svc);

        reg = context.registerService(UserManagementService.class, svc, null);
        System.out.println("[user-management-impl] UserManagementService registered");
    }

    @Override
    public void stop(BundleContext context) {
        if (reg != null) reg.unregister();
        System.out.println("[user-management-impl] stopped");
    }

    private void seedUsers(UserManagementServiceImpl svc) {
        svc.createUser("admin1", "admin1@demo.com", "ADMIN", null);
        svc.createUser("physioA", "physioa@demo.com", "PHYSIO", "Demo Clinic A");
        svc.createUser("physioB", "physiob@demo.com", "PHYSIO", "Demo Clinic B");
        svc.createUser("patientX", "patientx@demo.com", "PATIENT", null);
        svc.createUser("patientY", "patienty@demo.com", "PATIENT", null);

        System.out.println("[SEED] Users seeded (admin/physio/patient)");
    }
}
