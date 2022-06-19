package com.fintech.app.util;

import com.fintech.app.model.User;
import com.fintech.app.model.VerificationToken;
import com.fintech.app.request.UserRequest;
import com.fintech.app.response.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Component
@Slf4j
public class Util {

    @Bean
    public PasswordEncoder passwordEncoder() {
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

    public boolean validatePassword(String password, String cpassword) {
        return password.equals(cpassword);
    }

    public void resendVerificationTokenMail(User user,
                                            String applicationUrl,
                                            VerificationToken verificationToken) {
        // send email to user
        String url = applicationUrl
                + "/verifyRegistration?token="
                + verificationToken.getToken();
        log.info("Verification token: {} has been resend", url);


    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath();
    }

}
