package com.fintech.app.repository;

import com.fintech.app.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository
        extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}
