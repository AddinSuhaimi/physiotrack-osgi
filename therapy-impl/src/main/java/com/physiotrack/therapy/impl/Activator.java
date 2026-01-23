
package com.physiotrack.therapy.impl;

import com.physiotrack.therapy.api.TherapyManagementService;
import com.physiotrack.therapy.api.TherapyProgressService;
import com.physiotrack.therapy.api.model.PTProgram;
import com.physiotrack.therapy.api.model.PTActivity;
import com.physiotrack.therapy.api.model.OTProgram;
import com.physiotrack.therapy.api.model.OTActivity;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<TherapyManagementService> regMgmt;
    private ServiceRegistration<TherapyProgressService> regProgress;

    @Override

    public void start(BundleContext context) {
        TherapyManagementServiceImpl mgmtService = new TherapyManagementServiceImpl();
        seedTherapyPrograms(mgmtService);
        regMgmt = context.registerService(TherapyManagementService.class, mgmtService, null);
        TherapyProgressServiceImpl progressService = new TherapyProgressServiceImpl(mgmtService);
        regProgress = context.registerService(TherapyProgressService.class, progressService, null);
        System.out.println("[therapy-impl] TherapyManagementService and TherapyProgressService registered");
    }

    /**
     * Seed demo PT and OT programs and activities for demo users
     */
    private void seedTherapyPrograms(TherapyManagementServiceImpl mgmtService) {
        // PT Program for patientId 4
        Long ptPatientId = 4L;
        PTProgram ptProgram = new PTProgram();
        ptProgram.setPatientId(ptPatientId);
        mgmtService.savePTProgram(ptProgram);

        PTActivity pt1 = new PTActivity();
        pt1.setName("Knee Flexion");
        pt1.setCompleted(false);
        mgmtService.addPTActivity(ptPatientId, pt1);

        PTActivity pt2 = new PTActivity();
        pt2.setName("Quad Sets");
        pt2.setCompleted(false);
        mgmtService.addPTActivity(ptPatientId, pt2);

        // OT Program for patientId 5
        Long otPatientId = 5L;
        OTProgram otProgram = new OTProgram();
        otProgram.setPatientId(otPatientId);
        mgmtService.saveOTProgram(otProgram);

        OTActivity ot1 = new OTActivity();
        ot1.setName("Hand Stretch");
        ot1.setCompleted(false);
        mgmtService.addOTActivity(otPatientId, ot1);

        OTActivity ot2 = new OTActivity();
        ot2.setName("Grip Strength");
        ot2.setCompleted(false);
        mgmtService.addOTActivity(otPatientId, ot2);

        System.out.println("[SEED] Therapy programs and activities seeded for demo patients (IDs 4, 5)");
    }

    @Override
    public void stop(BundleContext context) {
        if (regMgmt != null) regMgmt.unregister();
        if (regProgress != null) regProgress.unregister();
        System.out.println("[therapy-impl] stopped");
    }
}
