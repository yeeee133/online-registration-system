package com.example.registration.service;

import com.example.registration.model.ApiResponse;
import com.example.registration.repository.ActivityRepository;
import com.example.registration.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class RegistrationService {
    private final ActivityRepository activityRepository;
    private final RegistrationRepository registrationRepository;

    public RegistrationService(ActivityRepository activityRepository, RegistrationRepository registrationRepository) {
        this.activityRepository = activityRepository;
        this.registrationRepository = registrationRepository;
    }

    @Transactional
    public ApiResponse<Map<String, Object>> register(Long activityId, String studentName) {
        if (studentName == null || studentName.trim().isEmpty()) {
            return ApiResponse.fail("studentName不能为空");
        }
        if (activityRepository.findById(activityId) == null) {
            return ApiResponse.fail("活动不存在");
        }
        if (registrationRepository.existsByActivityIdAndStudentName(activityId, studentName)) {
            return ApiResponse.fail("请勿重复报名");
        }

        int current = activityRepository.getCurrentPeople(activityId);
        int maxPeople = activityRepository.getMaxPeople(activityId);
        if (current >= maxPeople) {
            return ApiResponse.fail("活动名额已满");
        }

        registrationRepository.insert(activityId, studentName);
        activityRepository.increaseCurrentPeople(activityId);

        Map<String, Object> data = new HashMap<>();
        data.put("activityId", activityId);
        data.put("studentName", studentName);
        data.put("currentPeople", current + 1);
        data.put("maxPeople", maxPeople);
        return ApiResponse.ok("报名成功", data);
    }

    public Map<String, Object> count(Long activityId) {
        Map<String, Object> map = new HashMap<>();
        map.put("activityId", activityId);
        map.put("count", registrationRepository.countByActivityId(activityId));
        return map;
    }
}
