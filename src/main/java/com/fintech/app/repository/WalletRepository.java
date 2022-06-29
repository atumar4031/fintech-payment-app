package com.fintech.app.repository;
import com.fintech.app.model.User;
import com.fintech.app.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findWalletByUser(User user);
    Wallet findWalletByAccountNumber(String accountNumber);

}