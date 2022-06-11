package com.fintech.app.service;

import com.fintech.app.request.UserRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.UserResponse;

public interface UserService {
    BaseResponse<UserResponse> createUserAccount(UserRequest userRequest);
    BaseResponse<UserResponse> getUser(long userId);
}
