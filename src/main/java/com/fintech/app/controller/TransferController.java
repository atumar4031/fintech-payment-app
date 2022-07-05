package com.fintech.app.controller;

import com.fintech.app.model.LocalTransfer;
import com.fintech.app.model.Transfer;
import com.fintech.app.request.LocalTransferRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.service.LocalTransferService;
import lombok.RequiredArgsConstructor;
import com.fintech.app.request.TransferRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.FlwAccountResponse;
import com.fintech.app.model.FlwBank;
import com.fintech.app.request.FlwAccountRequest;
import com.fintech.app.response.OtherBankTransferResponse;
import com.fintech.app.service.impl.OtherBankTransferImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfers")
public class TransferController {

    @Autowired
    private OtherBankTransferImpl transactionService;
    
    private final LocalTransferService localTransferService;
    @PostMapping("/local")
    public BaseResponse<Transfer> makeLocalTransfer(@RequestBody LocalTransferRequest localTransferRequest){
        return localTransferService.makeLocalTransfer(localTransferRequest);
    }
    @GetMapping("/resolveLocalAccount/{accountNumber}")
    public BaseResponse<String> resolveLocalAccount(@PathVariable String accountNumber){
        return localTransferService.resolveLocalAccount(accountNumber);
    }

    @GetMapping("/banks")
    public List<FlwBank> getBanks(){
         return transactionService.getBanks();
    }

    @PostMapping("/resolveOtherBank")
    public BaseResponse<FlwAccountResponse> resolveOtherAccount(@RequestBody FlwAccountRequest flwAccountRequest){
        return transactionService.resolveAccount(flwAccountRequest);
    }

    @PostMapping("/otherBank")
    public BaseResponse<OtherBankTransferResponse> processTransfer(@Valid @RequestBody TransferRequest transferRequest){
        return transactionService.initiateOtherBankTransfer(transferRequest);
    }

}
