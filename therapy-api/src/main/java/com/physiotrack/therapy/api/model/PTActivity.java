package com.physiotrack.therapy.api.model;

public class PTActivity {
    private Long id;
    private String name;
    private String description;
    private boolean completed;
    private PTProgram program;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public PTProgram getProgram() { return program; }
    public void setProgram(PTProgram program) { this.program = program; }
}