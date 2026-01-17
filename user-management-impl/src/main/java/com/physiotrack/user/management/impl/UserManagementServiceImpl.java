package com.physiotrack.user.management.impl;

import com.physiotrack.user.management.api.UserManagementService;
import com.physiotrack.user.management.api.model.User;
import com.physiotrack.user.management.impl.repository.UserRepository;

import java.util.List;

public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository repo;

    public UserManagementServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public String ping() {
        return "user-management module OK (users=" + repo.count() + ")";
    }

    @Override
    public User createUser(String username, String email, String role, String clinicName) {
        User existing = repo.findByEmail(email);
        if (existing != null) return existing;

        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setRole(role);
        u.setClinicName(clinicName);
        u.setActive(true);

        return repo.save(u);
    }

    @Override
    public User findByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public User findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public List<User> listAll() {
        return repo.findAll();
    }

    @Override
    public List<User> listByRole(String role) {
        return repo.findByRole(role);
    }

    @Override
    public boolean deactivate(String email) {
        return repo.setActiveByEmail(email, false);
    }

    @Override
    public boolean reactivate(String email) {
        return repo.setActiveByEmail(email, true);
    }
}
