package com.fintech.app.util;

import com.fintech.app.model.User;
import com.fintech.app.request.UserRequest;
import com.fintech.app.response.UserResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Util {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    public User requestToUser(UserRequest userRequest) {
        return User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .bvn(userRequest.getBvn())
                .createdAt(LocalDateTime.now())
                .modifyAt(LocalDateTime.now())
                .build();
    }

    public UserResponse userToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .bvn(user.getBvn())
                .email(user.getEmail())
                .build();
    }

    public  boolean validatePassword(String password, String cpassword){
        return  password.equals(cpassword);
    }
}
