package com.fintech.app.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class FundWalletRequest {

    private String event;

    @JsonProperty("event.type")
    private String event_type;
    
    private Data data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Data {
        private Long id;

        @JsonProperty("tx_ref")
        private String txRef;
        private Long flw_ref;
        private String device_fingerPrint;
        private int amount;
        private String currency;
        private String narration;
        private String status;
        private String payment_type;
        private LocalDateTime created_at;
        private Long account_id;
        private Customer customer;
        private Card card;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Customer {
        private Long id;
        private String name;
        private String phone_number;
        private String email;
        private LocalDateTime created_at;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Card {
        private String first_6digits;
        private String last_4digits;
        private String issuer;
        private String country;
        private String type;
        private String expiry;
    }
}
