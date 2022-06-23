package com.fintech.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.app.controller.LoginController;
import com.fintech.app.request.LoginRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.JwtAuthResponse;
import com.fintech.app.service.LoginService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ContextConfiguration(classes = {LoginController.class})
@ExtendWith(SpringExtension.class)
public class LoginControllerUnitTest {
    @Autowired
    private LoginController loginController;

    @MockBean
    private LoginService loginService;

    @Test
    void testLogin() throws Exception {
        String message = "Login Successful";
        String token = "ebbbsbbvkvk";
        when(this.loginService.login((LoginRequest) any())).thenReturn(new BaseResponse<JwtAuthResponse>(HttpStatus.OK, message, new JwtAuthResponse(token)));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("adekunle@gmail.com");
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

}
