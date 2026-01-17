package com.physiotrack.personal.info.impl;

import com.physiotrack.personal.info.api.PersonalInfoService;
import org.osgi.service.component.annotations.Component;

@Component(service = PersonalInfoService.class)
public class PersonalInfoServiceImpl implements PersonalInfoService {

    @Override
    public String ping() {
        return "personal-info service OK";
    }
}
