package com.fintech.app.service.impl;

import com.fintech.app.model.*;
import com.fintech.app.repository.*;
import com.fintech.app.request.FlwWalletRequest;
import com.fintech.app.request.UserRequest;
import com.fintech.app.response.*;
import com.fintech.app.service.LocalTransferService;
import com.fintech.app.service.UserService;
import com.fintech.app.service.WalletService;
import com.fintech.app.util.RegistrationCompleteEvent;
import com.fintech.app.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private final TransferRepository transferRepository;

//    @Transactional
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
        Wallet wallet = walletRepository.findWalletByUser(user);
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

    @Override
    public BaseResponse<TransactionHistoryResponse> getTransactionHistory(Integer page, Integer size, String sortBy) {

        if (page == null) page = 0;
        if (size == null) size = 10;
        if (sortBy == null) sortBy = "createdAt";
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByEmail(userEmail);
        Wallet wallet = walletRepository.findWalletByUser(user);
        String userAccountNumber = wallet.getAccountNumber();
        Page<Transfer> transfers = transferRepository
                .findAllBySenderAccountNumberOrDestinationAccountNumber(userAccountNumber, userAccountNumber, pageable);
        List<TransactionHistoryDto> userHistory = new ArrayList<>();

        for (var transfer : transfers) {
            TransactionHistoryDto response = mapTransferToTransactionHistoryResponse(userAccountNumber,transfer);
            userHistory.add(response);
        }
        TransactionHistoryResponse response = TransactionHistoryResponse.builder()
                                                .content(userHistory)
                                                .page(pageable)
                                                .build();
        return new BaseResponse<>(HttpStatus.OK, "Transaction History retrieved", response);
    }

    private TransactionHistoryDto mapTransferToTransactionHistoryResponse(String userAccountNumber, Transfer transfer) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("E, dd-MMMM-yyyy HH:mm");
        boolean isSender = userAccountNumber.equals(transfer.getSenderAccountNumber());
        String amount = String.format("%.2f",transfer.getAmount());
        TransactionHistoryDto response = TransactionHistoryDto.builder()
                .id(transfer.getId())
                .name(isSender ? transfer.getDestinationFullName() : transfer.getSenderFullName())
                .bank(isSender ? transfer.getDestinationBank() : transfer.getSenderBankName())
                .type(transfer.getType())
                .transactionTime(dateFormat.format(transfer.getCreatedAt()))
                .amount(isSender ? "-" + amount : "+" + amount)
                .build();
        return response;
    }

}
