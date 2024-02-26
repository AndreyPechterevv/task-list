package com.example.tasklist.service;

import com.example.tasklist.domain.task.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    Task getById(Long id);

    List<Task> findAllByUserId(Long id);

    Task update(Task task);

    Task create(Task task, Long id);

    void delete(Long id);
}
