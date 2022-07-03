package com.fintech.app.repository;

import com.fintech.app.model.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TransferRepository extends PagingAndSortingRepository<Transfer, Long> {
    Page<Transfer>
    findAllBySenderAccountNumberOrDestinationAccountNumber
            (String sender, String recipient, Pageable pageable);
}
