package com.physiotrack.summary.api.model;

import java.time.LocalDate;

public class Badge {
    private String id;
    private String name;
    private String description;
    private boolean earned;
    private LocalDate earnedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isEarned() { return earned; }
    public void setEarned(boolean earned) { this.earned = earned; }
    public LocalDate getEarnedAt() { return earnedAt; }
    public void setEarnedAt(LocalDate earnedAt) { this.earnedAt = earnedAt; }
}
