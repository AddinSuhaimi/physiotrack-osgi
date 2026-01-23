package com.physiotrack.personal.info.impl;

import com.physiotrack.personal.info.api.PersonalInfoService;
import com.physiotrack.user.management.api.UserManagementService;
import com.physiotrack.user.management.api.model.User;

public class PersonalInfoServiceImpl implements PersonalInfoService {

    private final UserManagementService userMgr;

    // Dependency Injection via Constructor
    public PersonalInfoServiceImpl(UserManagementService userMgr) {
        this.userMgr = userMgr;
    }

    @Override
    public User getProfile(Long userId) {
        return userMgr.findById(userId);
    }

    @Override
    public boolean updateProfile(Long userId, String newAddress, String newPhone, String newProfileImage) {
        User user = userMgr.findById(userId);
        if (user == null) return false;

        // Update fields if they are not null
        if (newAddress != null) user.setAddress(newAddress);
        if (newPhone != null) user.setPhone(newPhone);
        if (newProfileImage != null) user.setProfileImageUrl(newProfileImage);

        // Save changes using the UserManagementService
        userMgr.updateUser(user);
        return true;
    }

    @Override
    public boolean changeLanguage(Long userId, String languageCode) {
        User user = userMgr.findById(userId);
        if (user == null) return false;

        user.setLanguage(languageCode);
        userMgr.updateUser(user);
        System.out.println("Language changed to " + languageCode + " for user " + user.getUsername());
        return true;
    }
}
