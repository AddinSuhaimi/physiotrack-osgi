package com.physiotrack.progress.tracking.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.physiotrack.progress.tracking.api.ProgressTrackingService;
import com.physiotrack.progress.tracking.impl.repository.TreatmentReportRepository;

public class Activator implements BundleActivator {

    private ServiceRegistration<ProgressTrackingService> trackingReg;

    private TreatmentReportRepository treatmentReportRepository;

    @Override
    public void start(BundleContext context) throws Exception {

        // Create repository
        treatmentReportRepository = new TreatmentReportRepository();

        // Create service implementations
        ProgressTrackingService progressTrackingService = new ProgressTrackingServiceImpl(treatmentReportRepository);

        // Register services in OSGi
        trackingReg = context.registerService(ProgressTrackingService.class, progressTrackingService, null);

        System.out.println("[progress-tracking-impl] Progress Tracking Service registered");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (trackingReg != null) trackingReg.unregister();
        System.out.println("[progress-tracking-impl] stopped");
    }

}
