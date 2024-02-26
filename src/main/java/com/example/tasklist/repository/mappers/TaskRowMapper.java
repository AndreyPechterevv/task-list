package com.example.tasklist.repository.mappers;

import com.example.tasklist.domain.task.Status;
import com.example.tasklist.domain.task.Task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TaskRowMapper {

    public static Task mapRow(ResultSet rs) throws SQLException {
        if (rs.next()) {
            Task task = new Task();
            task.setId(rs.getLong("task_id"));
            task.setTitle(rs.getString("task_title"));
            task.setDescription(rs.getString("task_description"));
            task.setStatus(Status.valueOf(rs.getString("task_status")));
            Timestamp timestamp = rs.getTimestamp("task_expiration_date");
            if (timestamp != null) {
                task.setExpirationData(timestamp.toLocalDateTime());
            }
            return task;
        }
        return null;
    }

    public static List<Task> mapRows(ResultSet rs) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        while (rs.next()) {
            Task task = new Task();
            task.setId(rs.getLong("task_id"));
            if (!rs.wasNull()){
                task.setTitle(rs.getString("task_title"));
                task.setDescription(rs.getString("task_description"));
                task.setStatus(Status.valueOf(rs.getString("task_status")));
                Timestamp timestamp = rs.getTimestamp("task_expiration_date");
                if (timestamp != null) {
                    task.setExpirationData(timestamp.toLocalDateTime());
                }
                tasks.add(task);
            }

        }
        return tasks;
    }
}
