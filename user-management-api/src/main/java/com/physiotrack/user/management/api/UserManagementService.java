package com.physiotrack.user.management.api;

import com.physiotrack.user.management.api.model.User;

import java.util.List;

public interface UserManagementService {

    // keep for your ping menu
    String ping();

    // core functions used by other modules
    User createUser(String username, String email, String role, String clinicName);
    User findByEmail(String email);
    User findById(Long id);
    User updateUser(User user);

    List<User> listAll();
    List<User> listByRole(String role);
    List<User> findPatientsByPhysioId(Long physioId);

    boolean deactivate(String email);
    boolean reactivate(String email);
    boolean assignPatientToPhysio(Long patientId, Long physioId);
    boolean updateSharedJournalStatus(Long userId, boolean isShared);
}
