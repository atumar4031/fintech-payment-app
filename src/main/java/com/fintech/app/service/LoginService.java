package com.fintech.app.service;

import com.fintech.app.request.LoginRequest;
import com.fintech.app.request.PasswordRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.JwtAuthResponse;
import org.springframework.http.ResponseEntity;

public interface LoginService {
    BaseResponse<JwtAuthResponse> login(LoginRequest loginRequest) throws Exception;
    BaseResponse<?> logout(String token);

    BaseResponse<String> changePassword(PasswordRequest passwordRequest);
}
