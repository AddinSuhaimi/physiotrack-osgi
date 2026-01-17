package com.physiotrack.user.management.api.model;

public class User {
    private Long id;
    private String username;
    private String email;
    private String role;      // "ADMIN" | "PHYSIO" | "PATIENT"
    private boolean active;
    private String clinicName; // only for PHYSIO (optional)

    public User() {}

    public User(Long id, String username, String email, String role, boolean active, String clinicName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.active = active;
        this.clinicName = clinicName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }
}
