package com.example.demo.mappers;

import com.example.demo.dtos.auth.UserResponse;
import com.example.demo.entities.User;

public class AuthMapper {
    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole()) // ✅ ahora coincide con tipo String
                .build();
    }
}
