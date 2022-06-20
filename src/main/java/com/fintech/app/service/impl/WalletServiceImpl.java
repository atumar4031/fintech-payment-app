package com.fintech.app.service.impl;

import com.fintech.app.model.*;
import com.fintech.app.request.FlwWalletRequest;
import com.fintech.app.response.FlwVirtualAccountResponse;
import com.fintech.app.service.WalletService;
import com.fintech.app.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
@RequiredArgsConstructor
@Service
@Slf4j
public class WalletServiceImpl implements WalletService {


    @Override
    public Wallet createWallet(FlwWalletRequest walletRequest) throws JSONException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer "+ Constant.AUTHORIZATION);

        FlwWalletRequest payload = generatePayload(walletRequest);

        HttpEntity<FlwWalletRequest> request = new  HttpEntity<>(payload, headers);

        FlwVirtualAccountResponse body = restTemplate.exchange(
                Constant.CREATE_VIRTUAL_ACCOUNT_API,
                HttpMethod.POST,
                request,
                FlwVirtualAccountResponse.class).getBody();

        return Wallet.builder()
                .accountNumber(body.getData().getAccountNumber())
                .balance(Double.parseDouble(body.getData().getAmount()))
                .bankName(body.getData().getBankName())
                .build();
    }

    private FlwWalletRequest generatePayload(FlwWalletRequest walletRequest) throws JSONException {
        FlwWalletRequest jsono = new FlwWalletRequest();
        jsono.setEmail(walletRequest.getEmail());
        jsono.set_permanent(true);
        jsono.setBvn(walletRequest.getBvn());
        jsono.setPhonenumber(walletRequest.getPhonenumber());
        jsono.setFirstname(walletRequest.getFirstname());
        jsono.setLastname(walletRequest.getLastname());
        jsono.setTx_ref("fintech app");
        jsono.setNarration("fintech");

        return jsono;
    }
}
