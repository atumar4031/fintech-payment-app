package com.fintech.app.service;

import com.fintech.app.model.User;
import com.fintech.app.model.VerificationToken;
import com.fintech.app.request.PasswordRequest;
import com.fintech.app.request.UserRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.UserResponse;
import com.fintech.app.response.WalletResponse;
import org.springframework.boot.configurationprocessor.json.JSONException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

public interface UserService {
    BaseResponse<UserResponse> createUserAccount(UserRequest userRequest, HttpServletRequest request) throws JSONException;
    BaseResponse<UserResponse> getUser(long userId);
    void saveVerificationTokenForUser(String token, User user);
    Boolean validateRegistrationToken(String token);
    VerificationToken generateNewToken(String oldToken);
    BaseResponse<WalletResponse> fetchUserWallet(User user);

}
