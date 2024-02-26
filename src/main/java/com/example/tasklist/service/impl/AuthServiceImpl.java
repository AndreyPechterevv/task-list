package com.example.tasklist.service.impl;

import com.example.tasklist.domain.user.User;
import com.example.tasklist.service.AuthService;
import com.example.tasklist.service.UserService;
import com.example.tasklist.web.dto.auth.JwtRequest;
import com.example.tasklist.web.dto.auth.JwtResponse;
import com.example.tasklist.web.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public JwtResponse login(JwtRequest loginRequest) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        User user = userService.getByUsername(loginRequest.getUsername());

        return JwtResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .accessToken(jwtTokenProvider.createAccessToken(user.getId(), user.getUsername(), user.getRoles()))
                .refreshToken(jwtTokenProvider.createRefreshToken(user.getId(), user.getUsername()))
                .build();
    }

    @Override
    public JwtResponse refresh(String refreshToken) {
        return jwtTokenProvider.refreshUserTokens(refreshToken);
    }
}
