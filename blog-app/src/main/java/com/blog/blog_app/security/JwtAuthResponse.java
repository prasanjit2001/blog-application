package com.blog.blog_app.security;


import com.blog.blog_app.dto.UserDto;
import lombok.Data;

@Data
public class JwtAuthResponse {
    private String token;
    private UserDto user;

}
