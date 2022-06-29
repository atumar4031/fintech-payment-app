package com.fintech.app.controller;

import com.fintech.app.request.LoginRequest;
import com.fintech.app.request.PasswordRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.JwtAuthResponse;
import com.fintech.app.service.LoginService;
import com.fintech.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public BaseResponse<JwtAuthResponse> login(@RequestBody LoginRequest loginRequest) throws Exception {

        return loginService.login(loginRequest);
    }

    @PostMapping("/logout")
    public BaseResponse<?> logout(String token) {
        return loginService.logout(token);
    }


    @PostMapping("user/changePassword")
    public BaseResponse<String> changePassword(@RequestBody PasswordRequest passwordRequest) {
        return loginService.changePassword(passwordRequest);
    }

    @PostMapping("/forgot-password")
    public BaseResponse<String> forgotPassword(@RequestBody PasswordRequest passwordRequest) throws MessagingException {
        return loginService.generateResetToken(passwordRequest);
    }

    @PostMapping("/reset-password")
    public BaseResponse<String> resetPassword(@RequestBody PasswordRequest passwordRequest, @RequestParam("token") String token) {
        return loginService.resetPassword(passwordRequest, token);
    }
}
