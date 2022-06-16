package com.fintech.app.services.impl;

import com.fintech.app.model.BlacklistedToken;
import com.fintech.app.repository.BlacklistedTokenRepository;
import com.fintech.app.repository.UserRepository;
import com.fintech.app.request.LoginRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.JwtAuthResponse;
import com.fintech.app.security.JwtTokenProvider;
import com.fintech.app.service.BlacklistService;
import com.fintech.app.service.impl.LoginServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {LoginServiceImpl.class})
@ExtendWith(SpringExtension.class)
class LoginServiceImplUnitTest {
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private BlacklistService blacklistService;

    @MockBean
    private BlacklistedTokenRepository blackListedTokenRepository;

    @MockBean
    private HttpServletRequest httpServletRequest;

    @MockBean
    private BlacklistedToken blacklistToken;

    @MockBean
    private HttpServletResponse httpServletResponse;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    UserRepository userRepository;

    @Autowired
    private LoginServiceImpl loginServiceImpl;



    @Test
    void testLogin() throws Exception {
        when(this.jwtTokenProvider.generateToken((Authentication) any())).thenReturn("ABC123");
        doNothing().when(this.httpServletResponse).setHeader((String) any(), (String) any());
        when(this.authenticationManager.authenticate((Authentication) any()))
                .thenReturn(new TestingAuthenticationToken("Principal", "Credentials"));

        LoginRequest loginDto = new LoginRequest();
        loginDto.setEmail("stan@gmail.com");
        loginDto.setPassword("1234");
        BaseResponse<JwtAuthResponse> actualLoginResult = this.loginServiceImpl.login(loginDto);
        assertEquals(HttpStatus.OK, actualLoginResult.getStatus());
        assertEquals("ABC123", actualLoginResult.getResult().getAccessToken());
        verify(this.jwtTokenProvider).generateToken((Authentication) any());
        verify(this.httpServletResponse).setHeader((String) any(), (String) any());
        verify(this.authenticationManager).authenticate((Authentication) any());
    }


    @Test
    void testLogout() {
        when(this.httpServletRequest.getHeader((String) any())).thenReturn("https://decagon.com");

        BlacklistedToken blackListedToken = new BlacklistedToken();
        String token = "abhfkjc";
        blackListedToken.setId(123L);
        blackListedToken.setToken("ABC123");
        when(this.blacklistService.blacklistToken((String) any())).thenReturn(blackListedToken);
        BaseResponse<?> actualLogoutResult = this.loginServiceImpl.logout("ABC123");
        assertEquals("Logout Successful", actualLogoutResult.getMessage());
        assertEquals(HttpStatus.OK, actualLogoutResult.getStatus());
        assertEquals(actualLogoutResult.getStatus(), HttpStatus.OK);
        verify(this.httpServletRequest).getHeader((String) any());
        verify(this.blacklistService).blacklistToken((String) any());
    }

}
