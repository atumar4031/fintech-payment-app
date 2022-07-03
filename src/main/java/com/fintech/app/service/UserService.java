package com.fintech.app.service;

import com.fintech.app.model.User;
import com.fintech.app.model.VerificationToken;
import com.fintech.app.request.UserRequest;
import com.fintech.app.response.*;
import org.springframework.boot.configurationprocessor.json.JSONException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {
    BaseResponse<UserResponse> createUserAccount(UserRequest userRequest, HttpServletRequest request) throws JSONException;
    BaseResponse<UserResponse> getUser(long userId);
    void saveVerificationTokenForUser(String token, User user);
    Boolean validateRegistrationToken(String token);
    VerificationToken generateNewToken(String oldToken);
    BaseResponse<WalletResponse> fetchUserWallet(User user);
    BaseResponse<TransactionHistoryResponse> getTransactionHistory(Integer page, Integer size, String sortBy);

}
