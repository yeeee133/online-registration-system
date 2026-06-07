package com.example.registration.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class ActivityCreateRequest {
    @NotBlank(message = "活动名称不能为空")
    private String name;

    @Min(value = 1, message = "活动人数上限至少为1")
    private Integer maxPeople;

    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? null : name.trim(); }
    public Integer getMaxPeople() { return maxPeople; }
    public void setMaxPeople(Integer maxPeople) { this.maxPeople = maxPeople; }
}
