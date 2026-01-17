package com.physiotrack.user.management.impl.repository;

import com.physiotrack.user.management.api.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserRepository {

    private final ConcurrentHashMap<Long, User> byId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> idByEmailLower = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public long count() {
        return byId.size();
    }

    public User save(User u) {
        if (u.getEmail() == null) throw new IllegalArgumentException("email is required");
        String emailKey = u.getEmail().toLowerCase(Locale.ROOT);

        if (u.getId() == null) {
            u.setId(seq.getAndIncrement());
        }

        byId.put(u.getId(), u);
        idByEmailLower.put(emailKey, u.getId());
        return u;
    }

    public User findById(Long id) {
        if (id == null) return null;
        return byId.get(id);
    }

    public User findByEmail(String email) {
        if (email == null) return null;
        Long id = idByEmailLower.get(email.toLowerCase(Locale.ROOT));
        return id == null ? null : byId.get(id);
    }

    public List<User> findAll() {
        return new ArrayList<>(byId.values());
    }

    public List<User> findByRole(String role) {
        if (role == null) return List.of();
        String needle = role.trim().toUpperCase(Locale.ROOT);
        List<User> out = new ArrayList<>();
        for (User u : byId.values()) {
            if (u.getRole() != null && u.getRole().trim().toUpperCase(Locale.ROOT).equals(needle)) {
                out.add(u);
            }
        }
        return out;
    }

    public boolean setActiveByEmail(String email, boolean active) {
        User u = findByEmail(email);
        if (u == null) return false;
        u.setActive(active);
        save(u);
        return true;
    }
}
