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

    private List<TransactionHistoryDto> content;
    private Pageable page;
    private int currentPage;
    private int totalPages;
    private Long totalElements;
    private int numberOfElements;
    private boolean last;
    private boolean first;

    /*
    "totalPages": 11,
    "totalElements": 208,
    "last": false,
    "numberOfElements": 20,
    "number": 0,
    "sort": {
        "unsorted": true,
        "sorted": false,
        "empty": true
    },
    "first": true,
    "size": 20,
    "empty": false
     */

}
