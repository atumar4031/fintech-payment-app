package com.fintech.app.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionHistoryResponse {
    private Long id;
    private String name;
    private String bank;
    private String transactionTime;
    private String type;
    private String amount;

}
