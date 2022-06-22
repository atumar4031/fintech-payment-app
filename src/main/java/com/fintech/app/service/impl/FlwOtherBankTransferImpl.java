package com.fintech.app.service.impl;

import com.fintech.app.model.Transfer;
import com.fintech.app.model.User;
import com.fintech.app.model.Wallet;
import com.fintech.app.repository.TransferRepository;
import com.fintech.app.repository.UserRepository;
import com.fintech.app.repository.WalletRepository;
import com.fintech.app.request.FLWTransferRequest;
import com.fintech.app.response.FlwAccountResponse;
import com.fintech.app.model.FlwBank;
import com.fintech.app.request.FlwAccountRequest;
import com.fintech.app.response.FlwBankResponse;
import com.fintech.app.response.TransferResponse;
import com.fintech.app.service.FlwOtherBankTransferService;
import com.fintech.app.service.TransferService;
import com.fintech.app.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FlwOtherBankTransferImpl implements TransferService, FlwOtherBankTransferService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransferRepository transferRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public FlwOtherBankTransferImpl(UserRepository userRepository,
                                    WalletRepository walletRepository,
                                    TransferRepository transferRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transferRepository = transferRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<FlwBank> getBanks(String country) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ Constant.AUTHORIZATION);

        HttpEntity<FlwBankResponse> request = new  HttpEntity<>(null, headers);

        FlwBankResponse flwBankResponse = restTemplate.exchange(
                Constant.GET_BANKS_API +"/"+ country,
                HttpMethod.GET,
                request,
                FlwBankResponse.class).getBody();


        return flwBankResponse.getData();
    }

    @Override
    public FlwAccountResponse resolveAccount(FlwAccountRequest flwAccountRequest) {


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ Constant.AUTHORIZATION);

        HttpEntity<FlwAccountRequest> request = new  HttpEntity<>(flwAccountRequest, headers);

        FlwAccountResponse response = restTemplate.exchange(
                Constant.RESOLVE_ACCOUNT_API,
                HttpMethod.POST,
                request,
                FlwAccountResponse.class).getBody();

        return response;
    }



    @Override
    public String FlwInitiateTransfer(long userId, FLWTransferRequest transferRequest) {

        // find user details
        User user = retrieveUserDetails(userId);
        // validate pin
        if(!validatePin(transferRequest.getPin(), user))
            return "invalid pin";

        // validate amount
        if(!validateRequestBalance(transferRequest.getAmount()))
            return "invalid amount";

        if(!validateWalletBalance(transferRequest.getAmount(), user))
            return "insufficient balance";

        // save transfer
        saveTransactions(user, transferRequest);

        //call API
        // TODO: API CALL
        return "Transaction Success";
    }

    private void saveTransactions(User user, FLWTransferRequest transferRequest) {
        String clientReference = UUID.randomUUID().toString();
        Wallet wallet = walletRepository.findWalletByUser(user).get();
        int amount = Integer.parseInt(transferRequest.getAmount());
        double balance = wallet.getBalance() - amount;
        wallet.setBalance(balance);

        Transfer transfer = Transfer.builder()
                .amount(Integer.parseInt(transferRequest.getAmount()))
                .clientRef(clientReference)
                .flwRef(clientReference)
                .narration(transferRequest.getNarration())
                .status("PENDING")
                .destinationAccountNumber(transferRequest.getDestinationAccountNumber())
                .destinationBank(transferRequest.getDestinationBank())
                .createdAt(LocalDateTime.now())
                .modifyAt(LocalDateTime.now())
                .user(user)
                .build();

        walletRepository.save(wallet);
        transferRepository.save(transfer);
    }


    private User retrieveUserDetails(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user;
    }


    private boolean validatePin(String pin, User user) {
        return passwordEncoder.matches(pin, user.getPin());
    }

    private boolean validateRequestBalance(String requestAmount) {
       return Integer.parseInt(requestAmount) > 0;
    }

    private boolean validateWalletBalance(String requestAmount,User user){
        Wallet wallet = walletRepository.findWalletByUser(user).get();
        return wallet.getBalance() >= Integer.parseInt(requestAmount);
    }


    @Override
    public TransferResponse findTransfer(long id) {
        return null;
    }

    @Override
    public List<TransferResponse> findAllTransfers() {
        return null;
    }
}
