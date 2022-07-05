package com.fintech.app.service.impl;

import com.fintech.app.model.Transfer;
import com.fintech.app.model.User;
import com.fintech.app.model.Wallet;
import com.fintech.app.repository.TransferRepository;
import com.fintech.app.repository.UserRepository;
import com.fintech.app.repository.WalletRepository;
import com.fintech.app.request.OtherBankTransferRequest;
import com.fintech.app.request.TransferRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.FlwAccountResponse;
import com.fintech.app.model.FlwBank;
import com.fintech.app.request.FlwAccountRequest;
import com.fintech.app.response.FlwBankResponse;
import com.fintech.app.response.OtherBankTransferResponse;
import com.fintech.app.service.TransferService;
import com.fintech.app.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class OtherBankTransferImpl implements TransferService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransferRepository transferRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public OtherBankTransferImpl(UserRepository userRepository,
                                 WalletRepository walletRepository,
                                 TransferRepository transferRepository,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transferRepository = transferRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<FlwBank> getBanks() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ Constant.AUTHORIZATION);

        HttpEntity<FlwBankResponse> request = new  HttpEntity<>(null, headers);

        FlwBankResponse flwBankResponse = restTemplate.exchange(
                Constant.GET_BANKS_API +"/NG",
                HttpMethod.GET,
                request,
                FlwBankResponse.class).getBody();


        return flwBankResponse.getData();
    }

    @Override
    public BaseResponse<FlwAccountResponse> resolveAccount(FlwAccountRequest flwAccountRequest) {


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

        return new BaseResponse<>(HttpStatus.OK, "Account Resolved", response);
    }


    @Override
    public BaseResponse<OtherBankTransferResponse> initiateOtherBankTransfer(TransferRequest transferRequest) {
        OtherBankTransferResponse response = new OtherBankTransferResponse();
        try{
           User user = retrieveUserDetails(transferRequest.getUserId());
           if(!validatePin(transferRequest.getPin(), user))
               new BaseResponse<>(HttpStatus.BAD_REQUEST, "Pin error", "invalid pin");
           if(!validateRequestBalance(transferRequest.getAmount()))
               new BaseResponse<>(HttpStatus.BAD_REQUEST, "amount error", "invalid amount");
           if(!validateWalletBalance(transferRequest.getAmount(), user))
               new BaseResponse<>(HttpStatus.BAD_REQUEST, "balance error", "insufficient balance");
           // save transfer
           Transfer transfer = saveTransactions(user, transferRequest);
           //call API
           response = otherBankTransfer(transferRequest, transfer.getClientRef());

       }catch (RuntimeException runtimeException){
           runtimeException.printStackTrace();
       }
         return new BaseResponse<>(HttpStatus.OK, "Transfer completed", response);
    }

    private User retrieveUserDetails(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user;
    }

    private boolean validatePin(String pin, User user) {
        return passwordEncoder.matches(pin, user.getPin());
    }

    private boolean validateRequestBalance(Double requestAmount) {
        return requestAmount > 0;
    }

    private boolean validateWalletBalance(Double requestAmount,User user){
        Wallet wallet = walletRepository.findWalletByUser(user);
        return wallet.getBalance() >= requestAmount;
    }

    private OtherBankTransferResponse otherBankTransfer(TransferRequest transferRequest, String clientRef){

        OtherBankTransferRequest otherBankTransferRequest = OtherBankTransferRequest.builder()
                .accountBank(transferRequest.getBankCode())
                .accountNumber(transferRequest.getAccountNumber())
                .amount(transferRequest.getAmount())
                .currency("NGN")
                .narration(transferRequest.getNarration())
                .reference(clientRef)
                .debitCurrency("NGN")
                .build();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ Constant.AUTHORIZATION);

        HttpEntity<OtherBankTransferRequest> request = new  HttpEntity<>(otherBankTransferRequest, headers);

        OtherBankTransferResponse response = restTemplate.exchange(
                Constant.OTHER_BANK_TRANSFER_API,
                HttpMethod.POST,
                request,
                OtherBankTransferResponse.class).getBody();
        return response;
    }

    private Transfer saveTransactions(User user, TransferRequest transferRequest) {

        String clientReference = UUID.randomUUID().toString();
        Wallet wallet = walletRepository.findWalletByUser(user);
        int amount = transferRequest.getAmount().intValue();
        double balance = wallet.getBalance() - amount;
        wallet.setBalance(balance);

        Transfer transfer = Transfer.builder()
                .amount(transferRequest.getAmount())
                .clientRef(clientReference)
                .flwRef(clientReference)
                .type("OTHER")
                .narration(transferRequest.getNarration())
                .status(Constant.STATUS)
                .senderFullName(user.getFirstName() + " " + user.getLastName())
                .senderAccountNumber(wallet.getAccountNumber())
                .senderBankName(wallet.getBankName())
                .destinationAccountNumber(transferRequest.getAccountNumber())
                .destinationBank(transferRequest.getBankCode())
                .destinationFullName(transferRequest.getAccountName())
                .createdAt(LocalDateTime.now())
                .modifyAt(LocalDateTime.now())
                .user(user)
                .build();

        walletRepository.save(wallet);

        return transferRepository.save(transfer);

    }
}
