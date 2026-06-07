package com.example.registration.controller;

import com.example.registration.model.ActivityCreateRequest;
import com.example.registration.model.ActivityDto;
import com.example.registration.model.ApiResponse;
import com.example.registration.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> create(@Validated @RequestBody ActivityCreateRequest request, HttpSession session) {
        Object role = session.getAttribute("LOGIN_ROLE");
        if (role == null) {
            return ResponseEntity.status(401).body(ApiResponse.fail("请先登录"));
        }
        if (!"ADMIN".equals(String.valueOf(role))) {
            return ResponseEntity.status(403).body(ApiResponse.fail("仅管理员可创建活动"));
        }
        activityService.create(request.getName(), request.getMaxPeople());
        return ResponseEntity.ok(ApiResponse.ok("创建活动成功", null));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<ActivityDto>>> list() {
        return ResponseEntity.ok(ApiResponse.ok("查询成功", activityService.list()));
    }

    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<ActivityDto>> detail(@RequestParam Long id) {
        ActivityDto dto = activityService.detail(id);
        if (dto == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("活动不存在"));
        }
        return ResponseEntity.ok(ApiResponse.ok("查询成功", dto));
    }
}
