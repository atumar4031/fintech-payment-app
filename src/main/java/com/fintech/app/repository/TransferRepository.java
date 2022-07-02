package com.fintech.app.repository;

import com.fintech.app.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer>
    findAllBySenderAccountNumberOrDestinationAccountNumberOrderByCreatedAtDesc
            (String sender, String recipient);
}
