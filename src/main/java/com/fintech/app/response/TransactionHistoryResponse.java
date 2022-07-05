package com.fintech.app.response;

import lombok.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionHistoryResponse {

    List<TransactionHistoryDto> content;
    Pageable page;

}
