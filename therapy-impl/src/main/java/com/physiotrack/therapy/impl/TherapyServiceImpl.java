package com.physiotrack.therapy.impl;

import com.physiotrack.therapy.api.TherapyService;
import org.osgi.service.component.annotations.Component;

@Component(service = TherapyService.class)
public class TherapyServiceImpl implements TherapyService {

    @Override
    public String ping() {
        return "therapy service OK";
    }
}
