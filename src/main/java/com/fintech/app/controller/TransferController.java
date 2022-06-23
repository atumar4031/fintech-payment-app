package com.fintech.app.controller;


import com.fintech.app.request.TransferRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.FlwAccountResponse;
import com.fintech.app.model.FlwBank;
import com.fintech.app.request.FlwAccountRequest;
import com.fintech.app.service.impl.FlwOtherBankTransferImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    @Autowired
    private FlwOtherBankTransferImpl transactionService;

    @GetMapping("/banks")
    public List<FlwBank> getBanks(){
         return transactionService.getBanks();
    }

    @PostMapping("/resolveOtherBank")
    public BaseResponse<FlwAccountResponse> resolveOtherAccount(@RequestBody FlwAccountRequest flwAccountRequest){
        return transactionService.resolveAccount(flwAccountRequest);
    }

    @PostMapping("/otherBank")
    public BaseResponse<String> processTransfer(@Valid @RequestBody TransferRequest transferRequest){
        return transactionService.initiateOtherbankTransfer(transferRequest);
    }

}
