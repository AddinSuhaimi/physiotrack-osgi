package com.physiotrack.user.management.impl;

import com.physiotrack.user.management.api.UserManagementService;

public class UserManagementServiceImpl implements UserManagementService {
    @Override
    public String ping() {
        return "user-management module OK";
    }
}
