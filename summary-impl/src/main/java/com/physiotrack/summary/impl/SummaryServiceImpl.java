package com.physiotrack.summary.impl;

import com.physiotrack.summary.api.SummaryService;
import org.osgi.service.component.annotations.Component;

@Component(service = SummaryService.class)
public class SummaryServiceImpl implements SummaryService {

    @Override
    public String ping() {
        return "summary service OK";
    }
}
