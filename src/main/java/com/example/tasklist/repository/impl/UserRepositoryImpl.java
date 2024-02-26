package com.example.tasklist.repository.impl;

import com.example.tasklist.domain.exception.ResourceMappingException;
import com.example.tasklist.domain.user.Role;
import com.example.tasklist.domain.user.User;
import com.example.tasklist.repository.DataSourceConfig;
import com.example.tasklist.repository.UserRepository;
import com.example.tasklist.repository.mappers.UserRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final DataSourceConfig dataSourceConfig;

    private final String FIND_BY_ID = """
            select u.id as user_id,
                   u.name as user_name,
                   u.username as user_username,
                   u.password as user_password,
                   ur.role as user_role_role,
                   t.id as task_id,
                   t.title as task_title,
                   t.description as task_description,
                   t.expiration_date as task_expiration_date,
                   t.status as task_status
            from users u
            left join users_roles ur on  u.id = ur.user_id
            left join users_tasks ut on u.id = ut.user_id
            left join tasks t on ut.task_id = t.id
            where u.id = ?""";

    private final String FIND_BY_USERNAME = """
            select u.id as user_id,
                   u.name as user_name,
                   u.username as user_username,
                   u.password as user_password,
                   ur.role as user_role_role,
                   t.id as task_id,
                   t.title as task_title,
                   t.description as task_description,
                   t.expiration_date as task_expiration_date,
                   t.status as task_status
            from users u
            left join users_roles ur on ur.user_id = u.id
            left join users_tasks ut on u.id = ut.user_id
            left join tasks t on ut.task_id = t.id
            where u.username = ?""";

    private final String UPDATE = """
            update users
            set name = ?,
                username = ?,
                password = ?
            where id = ?""";

    private final String CREATE = """
            insert into users(name,username,password)
            values (?,?,?)""";

    private final String INSERT_USER_ROLE = """
            insert into users_roles(user_id,role)
            values (?,?)""";

    private final String IS_TASK_OWNER = """
            select exists
            (select 1 from users_tasks
            where user_id = ?
            and task_id = ?)""";

    private final String DELETE = """
            delete from users
            where id = ?""";
    @Override
    public Optional<User> findById(Long id) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setLong(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()){
                return Optional.ofNullable(UserRowMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("exception while finding user by id");
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_USERNAME,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setString(1, username);
            try (ResultSet rs = preparedStatement.executeQuery()){
                return Optional.ofNullable(UserRowMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("exception while finding user by username");
        }
    }

    @Override
    public void update(User user) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2,user.getUsername());
            preparedStatement.setString(3,user.getPassword());
            preparedStatement.setLong(4, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("exception while updating user");
        }
    }

    @Override
    public void create(User user) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2,user.getUsername());
            preparedStatement.setString(3,user.getPassword());
            preparedStatement.executeUpdate();
            try (ResultSet rs = preparedStatement.getGeneratedKeys()){
                rs.next();
                user.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("exception while creating user");
        }
    }

    @Override
    public void insertUserRole(Long userId, Role role) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_ROLE);
            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2,role.name());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("exception while insert role to user");
        }
    }

    @Override
    public boolean isTaskOwner(Long userId, Long taskId) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(IS_TASK_OWNER);
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, taskId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                rs.next();
                return  rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("exception while insert role to user");
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_ROLE);
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("exception while deleting user");
        }
    }
}
