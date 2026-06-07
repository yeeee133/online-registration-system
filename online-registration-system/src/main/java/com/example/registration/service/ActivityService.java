package com.example.registration.service;

import com.example.registration.model.ActivityDto;
import com.example.registration.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public void create(String name, int maxPeople) {
        activityRepository.insert(name, maxPeople);
    }

    public List<ActivityDto> list() {
        return activityRepository.findAll();
    }

    public ActivityDto detail(Long id) {
        return activityRepository.findById(id);
    }
}
