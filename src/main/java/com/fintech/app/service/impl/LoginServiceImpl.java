package com.fintech.app.service.impl;

import com.fintech.app.model.User;
import com.fintech.app.repository.BlacklistedTokenRepository;
import com.fintech.app.repository.UserRepository;
import com.fintech.app.request.LoginRequest;
import com.fintech.app.request.PasswordRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.JwtAuthResponse;
import com.fintech.app.security.JwtTokenProvider;
import com.fintech.app.service.BlacklistService;
import com.fintech.app.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletResponse httpServletResponse;
    private final BlacklistService blacklistService;
    private final BlacklistedTokenRepository blackListedTokenRepository;
    private final HttpServletRequest httpServletRequest;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;



    @Override
    public BaseResponse<JwtAuthResponse> login(LoginRequest loginRequest) throws Exception {
        Authentication authentication;
        String token;
        String message = "Login Successful";

        try{
            Authentication auth =  new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),loginRequest.getPassword());

            authentication = authenticationManager.authenticate(auth);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenProvider.generateToken(authentication);
            httpServletResponse.setHeader("Authorization", token);
        }
        catch (BadCredentialsException ex){
            throw new Exception("incorrect user credentials", ex);
        }
        return new BaseResponse<>(HttpStatus.OK, message, new JwtAuthResponse(token));
    }
    @Override
    public BaseResponse<?> logout(String token) {

        token = httpServletRequest.getHeader("Authorization");

        blacklistService.blacklistToken(token);

        return new BaseResponse<>(HttpStatus.OK, "Logout Successful", null);

    }

    @Override
    public BaseResponse<String> changePassword(PasswordRequest passwordRequest) {

        if(!passwordRequest.getNewPassword().equals(passwordRequest.getConfirmPassword())){
            throw new RuntimeException("new password must be the same with confirm password");
        }

        String loggedInUsername =  SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(loggedInUsername);
        User user = userRepository.findByEmail(loggedInUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean matchPasswordWithOldPassword = passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword());

        if(!matchPasswordWithOldPassword){
            throw new RuntimeException("old password is not correct");
        }
        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));

        userRepository.save(user);
        return new BaseResponse<>(HttpStatus.OK, "password changed successfully", null);
    }


}
