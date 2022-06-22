package com.fintech.app.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FLWTransferRequest {
        @JsonProperty("account_bank")
        private String  destinationBank;

        @JsonProperty("account_number")
        private String  destinationAccountNumber;
        private String sourceAccountNumber;
        private String sourceBank;
        private String amount;
        private String  narration;
        private String  currency;
        private String  reference;

        private String pin;

        @JsonProperty("callback_url")
        private String  callbackUrl;

        @JsonProperty("debit_currency")
        private String  debitCurrency;

}
