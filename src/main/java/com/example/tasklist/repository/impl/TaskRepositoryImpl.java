package com.example.tasklist.repository.impl;

import com.example.tasklist.domain.exception.ResourceMappingException;
import com.example.tasklist.domain.task.Task;
import com.example.tasklist.repository.DataSourceConfig;
import com.example.tasklist.repository.TaskRepository;
import com.example.tasklist.repository.mappers.TaskRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepository {

    private final DataSourceConfig dataSourceConfig;
    private final String FIND_BY_ID = """
            select t.id as task_id,
                   t.title as task_title,
                   t.description as task_description,
                   t.expiration_date as task_expiration_date,
                   t.status as task_status
                   from tasks t
            where id = ?""";
    private final String FIND_ALL_BY_USER_ID = """
            SELECT t.id as task_id,
                   t.title as task_title,
                   t.description as task_description,
                   t.expiration_date as task_expiration_date,
                   t.status as task_status
                   from tasks t
                   join users_tasks ut on t.id = ut.task_id
            WHERE ut.user_id = ?""";

    private final String ASSIGN = """
            insert into users_tasks (task_id, user_id)
            values (?,?)""";

    private final String DELETE = "delete from tasks where id = ?";

    private final String UPDATE = """
            update tasks
            set title = ?,
            description = ?,
            expiration_date = ?,
            status = ?
            where id = ?
            """;
    private final String CREATE = """
            insert into tasks (title,description,expiration_date,status)
            values (?,?,?,?)""";

    @Override
    public Optional<Task> getById(Long id) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID);
            preparedStatement.setLong(1,id);
            try (ResultSet rs = preparedStatement.executeQuery()){
                return Optional.ofNullable(TaskRowMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("error while finding task");
        }
    }

    @Override
    public List<Task> findAllByUserId(Long id) {

        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_USER_ID);
            preparedStatement.setLong(1,id);
            try (ResultSet rs = preparedStatement.executeQuery()){
                return TaskRowMapper.mapRows(rs);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("error while finding all tasks");
        }
    }

    @Override
    public void assignToUserById(Long taskId, Long userId) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(ASSIGN);
            preparedStatement.setLong(1,taskId);
            preparedStatement.setLong(2,userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("error while assigning to user");
        }
    }

    @Override
    public void update(Task task) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE);
            preparedStatement.setString(1, task.getTitle());
            if (task.getDescription() == null){
                preparedStatement.setNull(2, Types.VARCHAR);
            } else {
                preparedStatement.setString(2,task.getDescription());
            }
            if (task.getExpirationData() == null){
                preparedStatement.setNull(3, Types.TIMESTAMP);
            } else {
                preparedStatement.setTimestamp(3, Timestamp.valueOf(task.getExpirationData()));
            }
            preparedStatement.setString(4, task.getStatus().name());
            preparedStatement.setLong(5, task.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("error while updating task");
        }
    }

    @Override
    public void create(Task task) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, task.getTitle());
            if (task.getDescription() == null){
                preparedStatement.setNull(2, Types.VARCHAR);
            } else {
                preparedStatement.setString(2,task.getDescription());
            }
            if (task.getExpirationData() == null){
                preparedStatement.setNull(3, Types.TIMESTAMP);
            } else {
                preparedStatement.setTimestamp(3, Timestamp.valueOf(task.getExpirationData()));
            }
            preparedStatement.setString(4, task.getStatus().name());
            preparedStatement.executeUpdate();
            try (ResultSet rs = preparedStatement.getGeneratedKeys()){
                rs.next();
                task.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("error while updating task");
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE);
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("error while deleting user");
        }
    }
}
