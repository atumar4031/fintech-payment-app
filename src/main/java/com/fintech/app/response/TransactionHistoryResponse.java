package com.fintech.app.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionHistoryResponse {
    private String name;
    private String bank;
    private LocalDateTime dateTime;
    private String type;
    private double amount;

}
