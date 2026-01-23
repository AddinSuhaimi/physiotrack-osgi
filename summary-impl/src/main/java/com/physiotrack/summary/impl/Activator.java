package com.physiotrack.summary.impl;

import com.physiotrack.summary.api.SummaryService;
import com.physiotrack.summary.impl.repository.SummaryRepository;
import com.physiotrack.summary.text.TextSummaryService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<SummaryService> reg;
    private ServiceRegistration<TextSummaryService> textReg;

    @Override
    public void start(BundleContext context) {
        SummaryRepository repo = new SummaryRepository();
        SummaryServiceImpl summarySvc = new SummaryServiceImpl(repo);
        reg = context.registerService(SummaryService.class, summarySvc, null);
        TextSummaryService textSvc = new com.physiotrack.summary.impl.TextSummaryService(summarySvc);
        textReg = context.registerService(TextSummaryService.class, textSvc, null);
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
