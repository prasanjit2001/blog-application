package com.blog.blog_app.services.impl;

import com.blog.blog_app.constant.AppConstants;
import com.blog.blog_app.dto.UserDto;
import com.blog.blog_app.entities.Role;
import com.blog.blog_app.entities.User;
import com.blog.blog_app.exceptions.ResourceNotFoundException;
import com.blog.blog_app.repositories.RoleRepository;
import com.blog.blog_app.repositories.UserRepository;
import com.blog.blog_app.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableCaching
public class UserServiceImpl implements UserService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDto registerNewUser(UserDto userDto) {
      User user= this.modelMapper.map(userDto,User.class);
       user.setPassword(this.passwordEncoder.encode(user.getPassword())); //encode the password
     Role role=  this.roleRepository.findById(AppConstants.NORMAL_USER).get();
     user.getRoles().add(role);
     User newuser=this.userRepository.save(user);
     return this.modelMapper.map(newuser,UserDto.class);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
     User user =  this.dtoTouser(userDto);
     User savedUser=this.userRepository.save(user);
        return this.userToDto(savedUser);
    }

    @Override
    @CachePut(value = "users", key = "#userId")
    public UserDto updateUser(UserDto userDto, Integer userId) {
        User user=this.userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User","id",userId));
       user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setAbout(userDto.getAbout());
        User updatedUser=this.userRepository.save(user);
        return this.userToDto(updatedUser);
    }

    @Override
    @Cacheable(value = "users", key = "#userId")
    public UserDto getUserById(Integer userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", " Id ", userId));

        return this.userToDto(user);
    }

    @Override
    @Cacheable(value = "users")
    public List<UserDto> getAllUsers() {
        List<User> users = this.userRepository.findAll();
        List<UserDto> userDtos = users.stream().map(user -> this.userToDto(user)).collect(Collectors.toList());

        return userDtos;
    }

    @Override
    @CacheEvict(value = "users", key = "#userId")
    public void deleteUser(Integer userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
        this.userRepository.delete(user);

    }
    public User dtoTouser(UserDto userDto){
        User user=this.modelMapper.map(userDto,User.class);
        return user;
    }

    public UserDto userToDto(User user){
        UserDto userDto=this.modelMapper.map(user,UserDto.class);
        return userDto;
    }
}
