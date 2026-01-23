package com.physiotrack.summary.impl;

import com.physiotrack.summary.api.SummaryService;
import com.physiotrack.summary.impl.repository.SummaryRepository;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<SummaryService> reg;
    private org.osgi.framework.ServiceRegistration<?> textReg;

    @Override
    public void start(BundleContext context) {
        SummaryRepository repo = new SummaryRepository();
        SummaryServiceImpl summarySvc = new SummaryServiceImpl(repo);
        reg = context.registerService(SummaryService.class, summarySvc, null);
        // Register an auxiliary text-only summary service (interface name as string)
        TextSummaryService textSvc = new TextSummaryService(summarySvc);
        textReg = context.registerService(new String[]{"com.physiotrack.summary.text.TextSummaryService"}, textSvc, null);
        System.out.println("[summary-impl] SummaryService registered");
        System.out.println("[summary-impl] TextSummaryService registered");
    }

    @Override
    public void stop(BundleContext context) {
        if (reg != null) reg.unregister();
        if (textReg != null) textReg.unregister();
        System.out.println("[summary-impl] stopped");
    }
}
