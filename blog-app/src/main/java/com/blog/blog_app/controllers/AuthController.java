package com.blog.blog_app.controllers;

import com.blog.blog_app.dto.JwtAuthRequest;
import com.blog.blog_app.dto.UserDto;
import com.blog.blog_app.entities.User;
import com.blog.blog_app.exceptions.ApiException;
import com.blog.blog_app.exceptions.ResourceNotFoundException;
import com.blog.blog_app.repositories.UserRepository;
import com.blog.blog_app.security.JwtAuthResponse;
import com.blog.blog_app.security.JwtTokenHelper;
import com.blog.blog_app.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ModelMapper mapper;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> createToken(@RequestBody JwtAuthRequest request) throws Exception {

        this.authenticate(request.getEmail(), request.getPassword());
        log.info("UserName is:::{}", request.getEmail());
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(request.getEmail());
        Optional<User> userOptional = this.userRepo.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        User user = userOptional.get();
        String token = this.jwtTokenHelper.generateToken(userDetails);

        // Map User entity to UserDto
        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(token);
        response.setUser(this.mapper.map(user, UserDto.class));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void authenticate(String email, String password) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        try {
            this.authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            log.error("Invalid Details for user: {}", email);
            throw new ApiException("Invalid username or password!");
        }
    }

    //register new user
    @PostMapping("/register")
    public ResponseEntity<UserDto>registerUser(@RequestBody UserDto userDto){
      UserDto registeredUser=  this.userService.registerNewUser(userDto);
      return new ResponseEntity<UserDto>(registeredUser,HttpStatus.CREATED);
    }

}
