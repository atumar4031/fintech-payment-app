package com.fintech.app.controller;
import com.fintech.app.request.UserRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.UserResponse;
import com.fintech.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public BaseResponse<UserResponse> createUserAccount(@Valid @RequestBody UserRequest userRequest){
        return userService.createUserAccount(userRequest);
    }

    @GetMapping("/register/{userid}")
    public BaseResponse<UserResponse> getUser(@PathVariable("userid") long userId){
        return userService.getUser(userId);
    }
}
