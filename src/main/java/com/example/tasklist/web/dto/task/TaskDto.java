package com.example.tasklist.web.dto.task;

import com.example.tasklist.domain.task.Status;
import com.example.tasklist.web.dto.validation.OnCreate;
import com.example.tasklist.web.dto.validation.OnUpdate;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class TaskDto {

    @NotNull(message = "id must be not null", groups = {OnUpdate.class})
    private Long id;

    @NotNull(message = "title must be not null", groups = {OnCreate.class, OnUpdate.class})
    @Length(max = 255, message = "title length must be smaller then {max} symbols", groups = {OnCreate.class, OnUpdate.class})
    private String title;

    @Length(max = 255, message = "name length must be smaller then {max} symbols", groups = {OnCreate.class, OnUpdate.class})
    private String description;

    private Status status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime expirationData;
}
