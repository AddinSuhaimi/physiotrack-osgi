package com.physiotrack.therapy.impl;

import com.physiotrack.therapy.api.TherapyService;

public class TherapyServiceImpl implements TherapyService {
    @Override
    public String ping() {
        return "therapy module OK";
    }
}
