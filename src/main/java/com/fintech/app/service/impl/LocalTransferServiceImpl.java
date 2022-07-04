package com.fintech.app.service.impl;

import com.fintech.app.model.Transfer;
import com.fintech.app.model.User;
import com.fintech.app.model.Wallet;
import com.fintech.app.repository.TransferRepository;
import com.fintech.app.repository.UserRepository;
import com.fintech.app.repository.WalletRepository;
import com.fintech.app.request.LocalTransferRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.service.LocalTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalTransferServiceImpl implements LocalTransferService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransferRepository transferRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public BaseResponse<Transfer> makeLocalTransfer(LocalTransferRequest transferRequest) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Wallet userWallet = walletRepository.findWalletByUser(user);

        User recipient = getUserByAccountNumber(transferRequest.getAccountNumber());

        Wallet recipientWallet = walletRepository.findWalletByUser(recipient);

        Double transferAmount = transferRequest.getAmount();
        Double userWalletBalance = userWallet.getBalance();
        if (transferAmount < 0) {
            return new BaseResponse<>(HttpStatus.BAD_REQUEST, "Invalid transfer amount", null);
        }
        if (transferAmount >= userWalletBalance) {
            return new BaseResponse<>(HttpStatus.BAD_REQUEST, "Insufficient funds", null);
        }

        if (!passwordEncoder.matches(transferRequest.getPin(), user.getPin())) {
            return new BaseResponse<>(HttpStatus.BAD_REQUEST, "Incorrect transfer pin", null);
        }

        String recipientName = recipient.getFirstName() + " " + recipient.getLastName();
        Transfer newTransfer = Transfer.builder()
                .senderFullName(user.getFirstName() + " " + user.getLastName())
                .senderAccountNumber(userWallet.getAccountNumber())
                .senderBankName(userWallet.getBankName())
                .destinationFullName(recipientName)
                .destinationAccountNumber(transferRequest.getAccountNumber())
                .destinationBank(recipientWallet.getBankName())
                .amount(transferRequest.getAmount())
                .narration(transferRequest.getNarration())
                .status("PENDING")
                .type("LOCAL")
                .createdAt(LocalDateTime.now())
                .clientRef(UUID.randomUUID().toString())
                .build();

        transferRepository.save(newTransfer);

        double recipientWalletBalance = recipientWallet.getBalance();

        userWallet.setBalance(userWalletBalance - transferAmount);

        recipientWallet.setBalance(recipientWalletBalance + transferAmount);

        walletRepository.save(userWallet);
        walletRepository.save(recipientWallet);

        newTransfer.setStatus("SUCCESSFUL");
        newTransfer.setCreatedAt(LocalDateTime.now());

        transferRepository.save(newTransfer);

        return new BaseResponse<>(HttpStatus.OK, "Transfer to " + recipientName + " successful", newTransfer);
    }

    @Override
    public BaseResponse<String> resolveLocalAccount(String accountNumber) {
        Wallet wallet = walletRepository.findWalletByAccountNumber(accountNumber);

        if(wallet != null){
            String accountName = wallet.getUser().getFirstName() + " "+
                    wallet.getUser().getLastName();
            return new BaseResponse<>(HttpStatus.OK, "account retrieved",accountName);
        }else{
            return new BaseResponse<>(HttpStatus.BAD_REQUEST, "Account not found", null);
        }
    }

    private User getUserByAccountNumber(String accountNumber) {
        Wallet wallet = walletRepository.findWalletByAccountNumber(accountNumber);
        return wallet.getUser();
    }
}
