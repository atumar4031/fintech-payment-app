package com.fintech.app.controller;
import com.fintech.app.model.User;
import com.fintech.app.model.VerificationToken;
import com.fintech.app.request.UserRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.UserResponse;
import com.fintech.app.service.UserService;
import com.fintech.app.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {
    private final UserService userService;
    private final Util utility;

    @PostMapping("/register")
    public BaseResponse<UserResponse> createUserAccount(@Valid @RequestBody UserRequest userRequest,
                                                        HttpServletRequest request) throws JSONException {
        return userService.createUserAccount(userRequest, request);
    }

    @GetMapping("/verifyRegistration")
    public String validateRegistrationToken(@RequestParam("token") String token){
        boolean isValid = userService.validateRegistrationToken(token);
        return isValid ? "User verified successfully" : "User verification failed";
    }

    @GetMapping("/resendVerificationToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request){
        VerificationToken verificationToken = userService.generateNewToken(oldToken);
        User user = verificationToken.getUser();
        utility.resendVerificationTokenMail(user, utility.applicationUrl(request), verificationToken);
        return "verification link send";
    }

    @GetMapping("/register/{userid}")
    public BaseResponse<UserResponse> getUser(@PathVariable("userid") long userId){
        return userService.getUser(userId);
    }


}
