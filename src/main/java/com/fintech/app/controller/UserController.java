package com.fintech.app.controller;
import com.fintech.app.model.User;
import com.fintech.app.model.VerificationToken;
import com.fintech.app.request.UserRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.UserResponse;
import com.fintech.app.response.WalletResponse;
import com.fintech.app.service.UserService;
import com.fintech.app.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private Util utility;

    @PostMapping("/register")
    public BaseResponse<UserResponse> createUserAccount(@Valid @RequestBody UserRequest userRequest,
                                                        HttpServletRequest request) throws JSONException {
        return userService.createUserAccount(userRequest, request);
    }

    @GetMapping("/verifyRegistration")
    public BaseResponse validateRegistrationToken(@RequestParam("token") String token){
        boolean isValid = userService.validateRegistrationToken(token);
        return isValid ? new BaseResponse<>(HttpStatus.OK, "User verified successfully", null)
                : new BaseResponse<>(HttpStatus.BAD_REQUEST, "User verification failed", null);
    }

    @GetMapping("/resendVerificationToken")
    public BaseResponse resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request){
        VerificationToken verificationToken = userService.generateNewToken(oldToken);
        User user = verificationToken.getUser();
        utility.resendVerificationTokenMail(user, utility.applicationUrl(request), verificationToken);
        return new BaseResponse(HttpStatus.OK, "verification link sent", null);
    }

    @GetMapping("/register/{userid}")
    public BaseResponse<UserResponse> getUser(@PathVariable("userid") long userId){
        return userService.getUser(userId);
    }

    @GetMapping("/user/get-wallet")
    public BaseResponse<WalletResponse> fetchUserWallet(User user) {
        return userService.fetchUserWallet(user);
    }


}
