package com.fintech.app.service;

import com.fintech.app.model.LocalTransfer;
import com.fintech.app.model.User;
import com.fintech.app.model.Wallet;
import com.fintech.app.repository.LocalTransferRepository;
import com.fintech.app.repository.UserRepository;
import com.fintech.app.repository.WalletRepository;
import com.fintech.app.request.LocalTransferRequest;
import com.fintech.app.service.impl.LocalTransferServiceImpl;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class LocalTransferServiceImplUnitTest {
    @Mock
    private  UserRepository userRepository;
    @Mock
    private  WalletRepository walletRepository;
    @Mock
    private  LocalTransferRepository localTransferRepository;
    @Mock
    private  PasswordEncoder passwordEncoder;
    private User user1;
    private User user2;
    private Wallet wallet1;
    private Wallet wallet2;
    private LocalTransfer localTransfer;

    @InjectMocks
    private LocalTransferServiceImpl localTransferService;


    @BeforeEach
    void setUp() {
        user1 = User.builder().firstName("Stan").lastName("Sender").email("stan@gmail.com").build();
        user2 = User.builder().firstName("Stan").lastName("Recipient").email("stan2@gmail.com").build();
        wallet1 = Wallet.builder().accountNumber("1234567890").user(user1).balance(10000.0).build();
        wallet2 = Wallet.builder().accountNumber("1234564890").user(user2).balance(2000.0).build();
        localTransfer = LocalTransfer.builder().transferId(1L).senderAccountName(user1.getFirstName() + " " + user1.getLastName())
                .senderAccountNumber(wallet1.getAccountNumber())
                .recipientAccountName(user2.getFirstName() + " " + user2.getLastName())
                .recipientAccountNumber(wallet2.getAccountNumber())
                .transferAmount(2000.0)
                .narration("Stan Sender")
                .status("SUCCESSFUL")
                .transferDate(LocalDateTime.now())
                .build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn(user1.getEmail());
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.of(user1));
        Mockito.when(walletRepository.findWalletByAccountNumber(anyString())).thenReturn(wallet2);
        Mockito.when(walletRepository.findWalletByUser(user1)).thenReturn(wallet1);
        Mockito.when(walletRepository.findWalletByUser(user2)).thenReturn(wallet2);
        Mockito.when(passwordEncoder.matches(any(), any())).thenReturn(true);
        Mockito.when(localTransferRepository.save(any())).thenReturn(localTransfer);
        when(walletRepository.save(any())).thenReturn(null);
    }

    @Test
    void makeLocalTransfer() {
        var response = localTransferService.makeLocalTransfer(new LocalTransferRequest(
                "1234", 2000.0, wallet2.getAccountNumber(), "Stan Sender"
        ));
        Assertions.assertThat(response.getMessage()).isEqualTo("Transfer to " + user2.getFirstName() + " " + user2.getLastName() + " successful");
        Assertions.assertThat(wallet2.getBalance()).isEqualTo(4000.0);
        Assertions.assertThat(wallet1.getBalance()).isEqualTo(8000.0);

    }

    @Test
    void resolveLocalAccount() {
    }
}