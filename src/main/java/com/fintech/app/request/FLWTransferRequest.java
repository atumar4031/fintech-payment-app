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
        private String  accountBank;

        @JsonProperty("account_number")
        private String  accountNumber;

        private Integer amount;
        private String  narration;
        private String  currency;
        private String  reference;

        @JsonProperty("callback_url")
        private String  callbackUrl;

        @JsonProperty("debit_currency")
        private String  debitCurrency;

}
