package com.fintech.app.service;

import com.fintech.app.model.FlwVirtualAccountResponse;
import com.fintech.app.model.User;
import com.fintech.app.model.Wallet;
import com.fintech.app.request.FlwWalletRequest;
import com.fintech.app.request.WalletRequest;
import com.fintech.app.service.impl.WalletServiceImpl;
import com.fintech.app.util.Constant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @InjectMocks
    private WalletServiceImpl walletService;

    private RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

    FlwVirtualAccountResponse flwVirtualAccountResponse;
    Wallet wallet;
    User user;
    FlwWalletRequest payload;
    WalletRequest walletRequest;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .firstName("Fintech user")
                .lastName("Fintech user")
                .email("fintech@gmail.com")
                .phoneNumber("1234567898")
                .bvn("123456799")
                .password("password")
                .build();

        walletRequest = WalletRequest.builder()
                .phonenumber("1234567898")
                .firstname("fintech")
                .lastname("payment")
                .email("fintech@gmail.com")
                .build();

        wallet = Wallet.builder()
                .id(1L)
                .bankName("Finteck bank")
                .balance(20000.0)
                .user(user)
                .build();

        payload = FlwWalletRequest.builder()
                .email(user.getEmail())
                .bvn(user.getBvn())
                .phonenumber(user.getPhoneNumber())
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .is_permanent(true)
                .narration("Fintech team")
                .tx_ref("Fintech")
                .build();
        flwVirtualAccountResponse = new FlwVirtualAccountResponse();
        flwVirtualAccountResponse.setMessage("Success");
        flwVirtualAccountResponse.setMessage("wallet created");
    }

    @Test
    @DisplayName("TEST: create wallet")
    void should_createWallet() throws JSONException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.add("Authorization", "Bearer "+ Constant.AUTHORIZATION);
//
//        HttpEntity<FlwWalletRequest> request = new HttpEntity<>(payload, headers);
//        when(restTemplate.exchange(
//                Constant.CREATE_VIRTUAL_ACCOUNT_API,
//                HttpMethod.POST,
//                request, FlwVirtualAccountResponse.class))
//                .thenReturn(ResponseEntity.of(Optional.of(new FlwVirtualAccountResponse())));
//        Wallet wallet1 = walletService.createWallet(payload);
//        assertThat(wallet1).isInstanceOf(Wallet.class);
    }
}