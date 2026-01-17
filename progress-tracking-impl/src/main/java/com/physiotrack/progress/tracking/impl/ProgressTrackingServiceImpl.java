package com.physiotrack.progress.tracking.impl;

import com.physiotrack.progress.tracking.api.ProgressTrackingService;
import org.osgi.service.component.annotations.Component;

@Component(service = ProgressTrackingService.class)
public class ProgressTrackingServiceImpl implements ProgressTrackingService {

    @Override
    public String ping() {
        return "progress-tracking service OK";
    }
}
