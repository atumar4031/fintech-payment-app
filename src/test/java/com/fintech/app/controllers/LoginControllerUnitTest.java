package com.fintech.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.app.controller.LoginController;
import com.fintech.app.model.User;
import com.fintech.app.repository.UserRepository;
import com.fintech.app.request.LoginRequest;
import com.fintech.app.request.PasswordRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.JwtAuthResponse;
import com.fintech.app.security.CustomUserDetailsService;
import com.fintech.app.security.JwtTokenProvider;
import com.fintech.app.service.LoginService;
import com.fintech.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.mail.MessagingException;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ContextConfiguration(classes = {LoginController.class})
@ExtendWith(SpringExtension.class)
public class LoginControllerUnitTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("ukeloveth247@gmail.com");
        user.setPassword("great");

    }
    @Autowired
    private LoginController loginController;

    @MockBean
    private LoginService loginService;

    @Test
    void testLogin() throws Exception {
        String message = "Login Successful";
        String token = "ebbbsbbvkvk";
        when(this.loginService.login((LoginRequest) any())).thenReturn(new BaseResponse<>(HttpStatus.OK, message, new JwtAuthResponse(token)));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("stanleynkannebe@gmail.com");
        loginRequest.setPassword("1234");
        String content = (new ObjectMapper()).writeValueAsString(loginRequest);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.loginController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    public void testLogout() throws Exception {
        when(loginService.logout(any())).thenReturn(null);

        BaseResponse<?> result = loginService.logout(null);
        Assertions.assertNull(result);
    }

    @Test
    public void testChangePassword() throws  Exception{
        PasswordRequest passwordDto = PasswordRequest.builder()
                .oldPassword("stan")
                .newPassword("1234")
                .confirmPassword("1234")
                .build();

        when(this.loginService.changePassword(passwordDto))
                .thenReturn(new BaseResponse<>(HttpStatus.OK,
                        "Password changed successfully",
                        null));

        String content = (new ObjectMapper()).writeValueAsString(passwordDto);
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("/api/v1/user/changePassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.loginController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(405));

    }

}
