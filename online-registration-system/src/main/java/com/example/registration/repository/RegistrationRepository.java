package com.example.registration.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class RegistrationRepository {
    private final JdbcTemplate jdbcTemplate;

    public RegistrationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByActivityIdAndStudentName(Long activityId, String studentName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM registration WHERE activity_id = ? AND student_name = ?",
                Integer.class, activityId, studentName);
        return count != null && count > 0;
    }

    public int insert(Long activityId, String studentName) {
        return jdbcTemplate.update(
                "INSERT INTO registration(activity_id, student_name, created_time) VALUES (?, ?, ?)",
                activityId, studentName, Timestamp.valueOf(LocalDateTime.now()));
    }

    public int countByActivityId(Long activityId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM registration WHERE activity_id = ?",
                Integer.class, activityId);
        return count == null ? 0 : count;
    }

    public List<Map<String, Object>> findByActivityId(Long activityId) {
        return jdbcTemplate.queryForList(
                "SELECT id, activity_id, student_name, created_time FROM registration WHERE activity_id = ? ORDER BY id DESC",
                activityId);
    }
}
