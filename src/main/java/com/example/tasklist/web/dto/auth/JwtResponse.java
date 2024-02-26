package com.example.tasklist.web.dto.auth;

import lombok.*;

//@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    private Long id;
    private String username;
    private String password;
    private String refreshToken;
    private String accessToken;
}
