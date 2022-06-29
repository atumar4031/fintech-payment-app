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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
@ExtendWith(MockitoExtension.class)
class LoginServiceImplUnitTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private BlacklistService blacklistService;

    @Mock
    private BlacklistedTokenRepository blackListedTokenRepository;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private BlacklistedToken blacklistToken;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    @InjectMocks
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

    @BeforeEach
    void setUp() {
//        when(authenticationManager.authenticate(any())).thenReturn();
        when(jwtTokenProvider.generateToken(any())).thenReturn("ABC123");
        user = User.builder().email("stan@gmail.com").password("1234").build();
    }

    @Test
    void testLogin() throws Exception {
//        doNothing().when(httpServletResponse.setHeader(anyString(), anyString()));

        LoginRequest loginDto = new LoginRequest();
        loginDto.setEmail("stan@gmail.com");
        loginDto.setPassword("1234");
        Authentication auth =  new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),loginDto.getPassword());
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
//        when(jwtTokenProvider.generateToken(any())).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        BaseResponse<JwtAuthResponse> actualLoginResult = loginServiceImpl.login(loginDto);
        Assertions.assertThat(HttpStatus.OK).isEqualTo(actualLoginResult.getStatus());
        assertEquals("Login Successful", actualLoginResult.getMessage());
        assertEquals("ABC123", actualLoginResult.getResult().getAccessToken());
        verify(jwtTokenProvider).generateToken(any());
        verify(httpServletResponse).setHeader(anyString(), anyString());
        verify(authenticationManager).authenticate(any());

//        Authentication auth =  new UsernamePasswordAuthenticationToken(
//                loginRequest.getEmail(),loginRequest.getPassword());

//        authentication = authenticationManager.authenticate(auth);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        token = jwtTokenProvider.generateToken(authentication);
//        httpServletResponse.setHeader("Authorization", token);
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
        PasswordRequest resetPasswordDto = new PasswordRequest("stan@gmail.com", "1234","stan", "stan");
        Mockito.when(jwtTokenProvider.getUsernameFromJwt(any())).thenReturn(user.getEmail());
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        String newPassword = "iou23iu23ioy3o73873ii";
        Mockito.when(passwordEncoder.encode(any())).thenReturn(newPassword);
        Mockito.when(userRepository.save(any())).thenReturn(user);
        BaseResponse<String> resetPassword = loginServiceImpl.resetPassword(resetPasswordDto, "hsdjksuiwue");
        org.assertj.core.api.Assertions.assertThat(resetPassword.getMessage()).isEqualTo("Password Reset Successfully");
    }


}
