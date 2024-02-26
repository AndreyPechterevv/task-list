package com.example.tasklist.web.dto.user;

import com.example.tasklist.web.dto.validation.OnCreate;
import com.example.tasklist.web.dto.validation.OnUpdate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserDto {

    @NotNull(message = "id must be not null", groups = {OnUpdate.class})
    private Long id;

    @NotNull(message = "name must be not null", groups = {OnCreate.class, OnUpdate.class})
    @Length(max = 255, message = "name length must be smaller then {max} symbols", groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @NotNull(message = "username must be not null", groups = {OnCreate.class, OnUpdate.class})
    @Length(max = 255, message = "username length must be smaller then {max} symbols", groups = {OnCreate.class, OnUpdate.class})
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "password must be not null", groups = {OnCreate.class, OnUpdate.class})
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "password confirmation must be not null", groups = {OnCreate.class})
    private String passwordConfirmation;
}
