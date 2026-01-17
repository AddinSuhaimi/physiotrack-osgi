package com.physiotrack.personal.info.impl;

import com.physiotrack.personal.info.api.PersonalInfoService;

public class PersonalInfoServiceImpl implements PersonalInfoService {
    @Override
    public String ping() {
        return "personal-info module OK";
    }
}
