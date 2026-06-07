package com.example.registration.model;

import java.time.LocalDateTime;

public class ActivityDto {
    private Long id;
    private String name;
    private Integer maxPeople;
    private Integer currentPeople;
    private LocalDateTime createdTime;

    public ActivityDto() {}

    public ActivityDto(Long id, String name, Integer maxPeople, Integer currentPeople, LocalDateTime createdTime) {
        this.id = id;
        this.name = name;
        this.maxPeople = maxPeople;
        this.currentPeople = currentPeople;
        this.createdTime = createdTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getMaxPeople() { return maxPeople; }
    public void setMaxPeople(Integer maxPeople) { this.maxPeople = maxPeople; }
    public Integer getCurrentPeople() { return currentPeople; }
    public void setCurrentPeople(Integer currentPeople) { this.currentPeople = currentPeople; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}
