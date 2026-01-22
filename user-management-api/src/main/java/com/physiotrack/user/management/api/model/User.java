package com.physiotrack.user.management.api.model;

public class User {
    private Long id;
    private String username;
    private String email;
    private String role;       // "ADMIN" | "PHYSIO" | "PATIENT"
    private boolean active;
    private String clinicName; // only for PHYSIO (optional)
    private String address;
    private String phone;
    private String gender;
    private boolean isTakenTest;
    private Long assignedPhysioId; // ID of the physio this patient belongs to
    private String profileImageUrl;
    private int level;
    private boolean sharedJournal;
    
    private String language = "en"; 

    public User() {}

    public User(Long id, String username, String email, String role, 
        boolean active, String clinicName, String address, String phone, 
        String gender, boolean isTakenTest, Long assignedPhysioId, String profileImageUrl, 
        int level, boolean sharedJournal, String language) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.active = active;
        this.clinicName = clinicName;
        this.address = address;
        this.phone = phone;
        this.gender = gender;
        this.isTakenTest = isTakenTest;
        this.assignedPhysioId = assignedPhysioId;
        this.profileImageUrl = profileImageUrl;
        this.level = level;
        this.sharedJournal = sharedJournal;
        this.language = language; 
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

    public String getAddress() { return address; }
    public void setAddress(String address){ this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone){ this.phone = phone; }

    public String getGender() { return gender; }
    public void setGender(String gender){ this.gender = gender; }

    public boolean isTakenTest() { return isTakenTest; }
    public void setIsTakenTest(boolean isTakenTest){ this.isTakenTest = isTakenTest; }

    public Long getAssignedPhysioId() { return assignedPhysioId; }
    public void setAssignedPhysioId(Long assignedPhysioId) { this.assignedPhysioId = assignedPhysioId; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl){ this.profileImageUrl = profileImageUrl; }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public boolean isSharedJournal() { return sharedJournal; }
    public void setSharedJournal(boolean sharedJournal) { this.sharedJournal = sharedJournal; } 

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}