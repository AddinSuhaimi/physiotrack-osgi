package com.physiotrack.progress.tracking.impl;

import com.physiotrack.progress.tracking.api.ProgressTrackingService;

public class ProgressTrackingServiceImpl implements ProgressTrackingService {
    @Override
    public String ping() {
        return "progress-tracking module OK";
    }
}
