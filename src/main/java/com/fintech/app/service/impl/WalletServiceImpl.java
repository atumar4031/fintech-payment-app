package com.fintech.app.service.impl;

import com.fintech.app.model.*;
import com.fintech.app.repository.TransferRepository;
import com.fintech.app.repository.UserRepository;
import com.fintech.app.repository.WalletRepository;
import com.fintech.app.request.FlwWalletRequest;
import com.fintech.app.request.FundWalletRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.FlwVirtualAccountResponse;
import com.fintech.app.response.FundWalletResponse;
import com.fintech.app.response.WalletResponse;
import com.fintech.app.service.WalletService;
import com.fintech.app.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransferRepository transferRepository;

    @Value("${FLW_SECRET_KEY}")
    private String AUTHORIZATION;

    @Override
    public Wallet createWallet(FlwWalletRequest walletRequest) throws JSONException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ AUTHORIZATION);

        FlwWalletRequest payload = generatePayload(walletRequest);

        HttpEntity<FlwWalletRequest> request = new  HttpEntity<>(payload, headers);

        FlwVirtualAccountResponse body = restTemplate.exchange(
                Constant.CREATE_VIRTUAL_ACCOUNT_API,
                HttpMethod.POST,
                request,
                FlwVirtualAccountResponse.class).getBody();

        assert body != null;
        return Wallet.builder()
                .accountNumber(body.getData().getAccountNumber())
                .balance(Double.parseDouble(body.getData().getAmount()))
                .bankName(body.getData().getBankName())
                .build();
    }

    @Override
    public BaseResponse<WalletResponse> fetchUserWallet() {
        String loggedInUsername =  SecurityContextHolder.getContext().getAuthentication().getName();
       User user = userRepository.findUserByEmail(loggedInUsername);
        if (user == null) {
            return new BaseResponse<>(HttpStatus.NOT_FOUND, "User not found", null);
        }
        Wallet wallet = walletRepository.findWalletByUser(user);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("E, dd-MMMM-yyyy HH:mm");
        WalletResponse response = WalletResponse.builder()
                .walletId(wallet.getId())
                .accountNumber(wallet.getAccountNumber())
                .balance(String.format("\u20a6%,.2f",wallet.getBalance()))
                .bankName(wallet.getBankName())
                .createdAt(dateFormat.format(wallet.getCreatedAt()))
                .updatedAt(dateFormat.format(wallet.getModifyAt()))
                .build();
        return new BaseResponse<>(HttpStatus.OK, "User wallet retrieved", response);
    }

//    @Override
//    public BaseResponse<FundWalletResponse> fundWallet(FundWalletRequest request) {
//        if (transferRepository.findByFlwRef(request.getData().getId()).isEmpty()) {
//            String tx_ref = request.getData().getTx_ref();
//            Wallet wallet = walletRepository.findWalletByTx_ref(tx_ref);
//            if(wallet == null) {
//                return new BaseResponse<>(HttpStatus.NOT_FOUND, "wallet not found", null);
//            }
//            User user
//            Transfer transfer = Transfer.builder()
//                    .flwRef(request.getData().getId())
//                    .destinationBank("FLUTTER_WAVE_WALLET")
//
//                    .senderFullName(request.getData().getCustomer().getName())
//                    .amount((double)request.getData().getAmount())
//                    .senderBankName(request.getData().getCard().getIssuer())
//                    .type("WALLET_FUND")
//                    .clientRef(UUID.randomUUID().toString())
//                    .status(request.getData().getStatus().toUpperCase())
//                    .senderAccountNumber("nil")
//                    .createdAt(LocalDateTime.now())
//                    .build();
//        }
//
//        return null;
//    }

    private FlwWalletRequest generatePayload(FlwWalletRequest walletRequest) throws JSONException {
        FlwWalletRequest jsono = new FlwWalletRequest();
        jsono.setEmail(walletRequest.getEmail());
        jsono.set_permanent(true);
        jsono.setBvn(walletRequest.getBvn());
        jsono.setPhonenumber(walletRequest.getPhonenumber());
        jsono.setFirstname(walletRequest.getFirstname());
        jsono.setLastname(walletRequest.getLastname());
        jsono.setTx_ref(UUID.randomUUID().toString());
        jsono.setNarration("fintech");

        return jsono;
    }
}
