package com.fintech.app.service;

import com.fintech.app.model.LocalTransfer;
import com.fintech.app.model.User;
import com.fintech.app.model.Wallet;
import com.fintech.app.repository.LocalTransferRepository;
import com.fintech.app.repository.UserRepository;
import com.fintech.app.repository.WalletRepository;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.WalletResponse;
import com.fintech.app.service.impl.LocalTransferServiceImpl;
import com.fintech.app.service.impl.UserServiceImpl;
import com.fintech.app.service.impl.WalletServiceImpl;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class FetchUserWalletUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletRepository walletRepository;
    private User user;
    private Wallet wallet;

    @InjectMocks
    private WalletServiceImpl walletService;

    @BeforeEach
    void  setUp(){
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        user = User.builder()
                .firstName("Stanley")
                .lastName("Gabriel")
                .email("stan@gmail.com")
                .build();

        wallet = Wallet.builder()
                .accountNumber("0012345678")
                .user(user)
                .bankName("Access")
                .build();
    }

    @Test
    void fetchUserWallet() {
        given(userRepository.findUserByEmail(any())).willReturn(user);
        when(walletRepository.findWalletByUser(any())).thenReturn(wallet);

        BaseResponse<WalletResponse> response = walletService.fetchUserWallet(user);

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void testForWhenUserIsNotLoggedIn(){
        when(userRepository.findUserByEmail(any())).thenReturn(null);
        BaseResponse<WalletResponse> response = walletService.fetchUserWallet(user);

        Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}