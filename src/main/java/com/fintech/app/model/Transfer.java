package com.fintech.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String destinationAccountNumber;
    private String destinationBank;
    private String destinationFullName;
    private double amount;
    private String narration;
    private String status;
    private String clientRef;
    private String flwRef;
    private String type;
    private String senderFullName;
    private String senderBankName;
    private String senderAccountNumber;

    private Date createdAt;

}
