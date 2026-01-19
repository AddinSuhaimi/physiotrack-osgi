package com.physiotrack.personal.info.api;

import com.physiotrack.user.management.api.model.User;

public interface PersonalInfoService {
    
    // UC04: Edit Profile (Address, Phone, etc.)
    boolean updateProfile(Long userId, String newAddress, String newPhone, String newProfileImage);

    // UC05: Change Language
    boolean changeLanguage(Long userId, String languageCode);

    // View Profile
    User getProfile(Long userId);
}
