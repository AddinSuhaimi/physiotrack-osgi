package com.physiotrack.summary.impl;

import com.physiotrack.summary.api.SummaryService;

public class SummaryServiceImpl implements SummaryService {
    @Override
    public String ping() {
        return "summary module OK";
    }
}
