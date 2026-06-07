package com.example.registration.repository;

import com.example.registration.model.ActivityDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ActivityRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<ActivityDto> rowMapper = (rs, rowNum) -> new ActivityDto(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getInt("max_people"),
            rs.getInt("current_people"),
            rs.getTimestamp("created_time").toLocalDateTime()
    );

    public ActivityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insert(String name, int maxPeople) {
        return jdbcTemplate.update(
                "INSERT INTO activity(name, max_people, current_people, created_time) VALUES (?, ?, 0, ?)",
                name, maxPeople, Timestamp.valueOf(LocalDateTime.now()));
    }

    public List<ActivityDto> findAll() {
        return jdbcTemplate.query("SELECT id, name, max_people, current_people, created_time FROM activity ORDER BY id DESC", rowMapper);
    }

    public ActivityDto findById(Long id) {
        List<ActivityDto> list = jdbcTemplate.query(
                "SELECT id, name, max_people, current_people, created_time FROM activity WHERE id = ?",
                rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int getCurrentPeople(Long id) {
        Integer value = jdbcTemplate.queryForObject("SELECT current_people FROM activity WHERE id = ?", Integer.class, id);
        return value == null ? 0 : value;
    }

    public int getMaxPeople(Long id) {
        Integer value = jdbcTemplate.queryForObject("SELECT max_people FROM activity WHERE id = ?", Integer.class, id);
        return value == null ? 0 : value;
    }

    public int increaseCurrentPeople(Long id) {
        return jdbcTemplate.update("UPDATE activity SET current_people = current_people + 1 WHERE id = ?", id);
    }
}
