package com.redhat.example.model;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Task {
    public enum State { COMPLETE, OPEN }

    @JsonIgnore
    public String id;
    public String description;
    public String role;
    public String eventType;
    @JsonIgnore
    public OffsetDateTime dateTime;
    @JsonIgnore
    public String correlation;

    public State state = State.OPEN;

    public Task(String id, String description, String role, String eventType, OffsetDateTime dateTime, String correlation) {
        this.id = id;
        this.description = description;
        this.role = role;
        this.eventType = eventType;
        this.dateTime = dateTime;
        this.correlation = correlation;
        this.state = State.OPEN;
    }

    public Task(String description, String role, String eventType, String correlation) {
        this.description = description;
        this.role = role;
        this.eventType = eventType;
        this.correlation = correlation;
        this.state = State.OPEN;
    }

    public Task() {
    }
}
