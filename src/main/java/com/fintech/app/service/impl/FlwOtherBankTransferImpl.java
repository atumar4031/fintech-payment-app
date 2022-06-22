package com.fintech.app.service.impl;

import com.fintech.app.response.FlwAccountResponse;
import com.fintech.app.model.FlwBank;
import com.fintech.app.request.FlwAccountRequest;
import com.fintech.app.response.FlwBankResponse;
import com.fintech.app.service.FlwOtherBankTransferService;
import com.fintech.app.service.TransferService;
import com.fintech.app.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class FlwOtherBankTransferImpl implements TransferService, FlwOtherBankTransferService {


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

}
