package com.fintech.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.app.controller.LoginController;
import com.fintech.app.model.BlacklistedToken;
import com.fintech.app.model.User;
import com.fintech.app.repository.BlacklistedTokenRepository;
import com.fintech.app.repository.UserRepository;
import com.fintech.app.request.LoginRequest;
import com.fintech.app.request.PasswordRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.JwtAuthResponse;
import com.fintech.app.security.CustomUserDetailsService;
import com.fintech.app.security.JwtTokenProvider;
import com.fintech.app.service.BlacklistService;
import com.fintech.app.service.impl.LoginServiceImpl;
import com.fintech.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.Optional;

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

    @MockBean
    private LoginServiceImpl loginServiceImpl;

    private  LoginService loginService;

    Authentication authentication = Mockito.mock(Authentication.class);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);

//    @Mock
//    private JwtTokenProvider jwtTokenProvider;
//    @Mock
    private CustomUserDetailsService userDetailsService;



    @InjectMocks
    UserServiceImpl userService;

    private User user;



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

    @Test
    public void testChangePassword() throws  Exception{
        when(this.loginService.changePassword(any())).thenReturn(new BaseResponse<>(HttpStatus.OK, "Check Your Email to Reset Your Password", null));

        PasswordRequest passwordDto = new PasswordRequest();
        passwordDto.setConfirmPassword("stan");
        passwordDto.setNewPassword("stan");
        passwordDto.setOldPassword("1234");
        String content = (new ObjectMapper()).writeValueAsString(passwordDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/v1/user/changePassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(LoginController.class)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(200));

    }

    @Test
    void generateToken() throws MessagingException {
        PasswordRequest passwordRequest = PasswordRequest.builder()
                .email(user.getEmail())
                .build();
        Mockito.when(userRepository.findUserByEmail(any())).thenReturn(user);
        Mockito.when(userDetailsService.loadUserByUsername(any())).thenReturn(new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), Collections.singleton(new SimpleGrantedAuthority("ROLE_PREMIUM"))));
        String token = "iuhiuhdhsjhkhaieooijowjeosdjkjskj";
        Mockito.when(jwtTokenProvider.generateToken(any())).thenReturn(token);
//        Mockito.when(emailService.send(any())).thenReturn(ResponseEntity.ok("Message sent successfully"));
        BaseResponse<String> result = loginService.generateResetToken(passwordRequest);
        org.assertj.core.api.Assertions.assertThat(result.getMessage()).isEqualTo("Check Your Email to Reset Your Password");


    }

    @Test
    void resetPassword(){
        PasswordRequest resetPasswordDto = new PasswordRequest("stanleynkannebe@gmail.com", "1234","stan", "stan");
        Mockito.when(jwtTokenProvider.getUsernameFromJwt(any())).thenReturn(user.getEmail());
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        String newPassword = "iou23iu23ioy3o73873ii";
        Mockito.when(passwordEncoder.encode(any())).thenReturn(newPassword);
        Mockito.when(userRepository.save(any())).thenReturn(user);
        BaseResponse<String> resetPassword = loginService.resetPassword(resetPasswordDto, "hsdjksuiwue");
        org.assertj.core.api.Assertions.assertThat(resetPassword.getMessage()).isEqualTo("Password Reset Successfully");
    }


}
