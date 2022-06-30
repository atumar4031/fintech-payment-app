package com.fintech.app.service.impl;

import com.fintech.app.model.User;
import com.fintech.app.model.VerificationToken;
import com.fintech.app.model.Wallet;
import com.fintech.app.repository.UserRepository;
import com.fintech.app.repository.VerificationTokenRepository;
import com.fintech.app.repository.WalletRepository;
import com.fintech.app.request.FlwWalletRequest;
import com.fintech.app.request.UserRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.UserResponse;
import com.fintech.app.response.WalletResponse;
import com.fintech.app.service.UserService;
import com.fintech.app.service.WalletService;
import com.fintech.app.util.RegistrationCompleteEvent;
import com.fintech.app.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;
    private final Util utility;
    private final WalletService walletService;
    private final WalletRepository walletRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public BaseResponse<UserResponse> createUserAccount(UserRequest userRequest, HttpServletRequest request) throws JSONException {

        if(userRepository.existsByEmail(userRequest.getEmail()))
            throw new RuntimeException("User already exist with this email");

        if (!utility.validatePassword(userRequest.getPassword(), userRequest.getConfirmPassword()))
                 throw new RuntimeException("Password not matched");

        User user = utility.requestToUser(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setPin(passwordEncoder.encode(userRequest.getPin()));
        User user1 = userRepository.save(user);

        // CREATE WALLET
        Wallet wallet = walletService.createWallet(FlwWalletRequest
                .builder()
                .email(user1.getEmail())
                .bvn(user1.getBvn())
                .firstname(user1.getFirstName())
                .lastname(user1.getLastName())
                .phonenumber(user1.getPhoneNumber())
                .build());
        wallet.setUser(user1);
        wallet.setBalance(0.0);
        walletRepository.save(wallet);

        // CALL EMAIL SERVICE
        publisher.publishEvent(new RegistrationCompleteEvent(
                user1,
                utility.applicationUrl(request)
        ));
        UserResponse userResponse = utility.userToResponse(user);
        return new BaseResponse<>(HttpStatus.CREATED, "Registration success", userResponse);
    }

    @Override
    public BaseResponse<UserResponse> getUser(long userId) {
        User user= userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserResponse userResponse = utility.userToResponse(user);
        return new BaseResponse<>(HttpStatus.OK, "user profile", userResponse);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }

    public void saveVerificationTokenForUser(String token, User user) {
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public Boolean validateRegistrationToken(String token) {
        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(token);
        if (verificationToken == null)
            return false;
        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if((verificationToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0){
            verificationTokenRepository.delete(verificationToken);
            return false;
        }
        user.setStatus(true);
        userRepository.save(user);
        return true;
    }

    @Override
    public VerificationToken generateNewToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public BaseResponse<WalletResponse> fetchUserWallet(User user) {
        String loggedInUsername =  SecurityContextHolder.getContext().getAuthentication().getName();
        user = userRepository.findUserByEmail(loggedInUsername);
        if (user == null) {
            return new BaseResponse<>(HttpStatus.NOT_FOUND, "User not found", null);
        }
        Wallet wallet = walletRepository.findWalletByUser(user).get();
        WalletResponse response = WalletResponse.builder()
                .walletId(wallet.getId())
                .accountNumber(wallet.getAccountNumber())
                .balance(wallet.getBalance())
                .bankName(wallet.getBankName())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getModifyAt())
                .build();
        return new BaseResponse<>(HttpStatus.OK, "User wallet retrieved", response);
    }

}
