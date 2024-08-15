package com.blog.blog_app.services;

import com.blog.blog_app.dto.UserDto;
import org.springframework.stereotype.Service;


import java.util.List;


public interface UserService {
    UserDto registerNewUser(UserDto user);
    UserDto createUser(UserDto user);
    UserDto updateUser(UserDto user,Integer userId);
    UserDto getUserById(Integer userId);
    List<UserDto>getAllUsers();
    void deleteUser(Integer userId);
}
