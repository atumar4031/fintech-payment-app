package com.fintech.app.service.impl;

import com.fintech.app.model.User;
import com.fintech.app.repository.UserRepository;
import com.fintech.app.request.UserRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.UserResponse;
import com.fintech.app.service.UserService;
import com.fintech.app.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final Util utility;

    @Override
    public BaseResponse<UserResponse> createUserAccount(UserRequest userRequest) {

        if(userRepository.existsByEmail(userRequest.getEmail()))
            throw new RuntimeException("User already exist with this email");

        if (!utility.validatePassword(userRequest.getPassword(), userRequest.getConfirmPassword()))
                 throw new RuntimeException("Password not matched");

        User user = utility.requestToUser(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userRepository.save(user);

        UserResponse userResponse = utility.userToResponse(user);
        return new BaseResponse<>(HttpStatus.CREATED, "Registration success", userResponse);
    }

    @Override
    public BaseResponse<UserResponse> getUser(long userId) {
        User user= userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserResponse userResponse = utility.userToResponse(user);
        return new BaseResponse<>(HttpStatus.OK, "user profile", userResponse);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }
}
