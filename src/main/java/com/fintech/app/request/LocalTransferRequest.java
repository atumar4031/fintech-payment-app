package com.fintech.app.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class LocalTransferRequest {
    @NotNull(message = "pin is required")
    String pin;
    @NotNull(message = "amount is required")
    Double amount;
    @NotNull(message = "account number is required")
    String accountNumber;
    @NotNull(message = "Narration is required")
    String narration;
}
