package com.fintech.app.controller;


import com.fintech.app.response.FlwAccountResponse;
import com.fintech.app.model.FlwBank;
import com.fintech.app.request.FlwAccountRequest;
import com.fintech.app.service.impl.FlwOtherBankTransferImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class TransferController {

    @Autowired
    private FlwOtherBankTransferImpl transactionService;

    @GetMapping("/banks/{country}")
    public List<FlwBank> getBanks(@PathVariable("country") String country){
         return transactionService.getBanks(country);
    }
    @PostMapping("/banks")
    public FlwAccountResponse resolveAccount(@RequestBody FlwAccountRequest flwAccountRequest){
        return transactionService.resolveAccount(flwAccountRequest);
    }

}
