package com.physiotrack.user.management.impl;

import com.physiotrack.user.management.api.UserManagementService;
import org.osgi.service.component.annotations.Component;

@Component(service = UserManagementService.class)
public class UserManagementServiceImpl implements UserManagementService {

    @Override
    public String ping() {
        return "user-management service OK";
    }
}
