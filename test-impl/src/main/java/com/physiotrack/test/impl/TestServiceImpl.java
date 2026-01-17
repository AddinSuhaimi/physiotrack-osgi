package com.physiotrack.test.impl;

import com.physiotrack.test.api.TestService;

public class TestServiceImpl implements TestService {
    @Override
    public String ping() {
        return "test module OK";
    }
}
