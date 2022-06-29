package com.fintech.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private Integer amount;
    private String narration;
    private String status;
    private String clientRef;

    private String flwRef;

    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime modifyAt;

}
