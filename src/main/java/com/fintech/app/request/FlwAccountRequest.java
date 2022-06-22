package com.fintech.app.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FlwAccountRequest {

     @JsonProperty("account_number")
     private String account_number;

     @JsonProperty("account_bank")
     private String account_bank;

}
