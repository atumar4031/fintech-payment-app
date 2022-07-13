package com.fintech.app.controller;

import com.fintech.app.model.FlwBank;
import com.fintech.app.model.Transfer;
import com.fintech.app.request.FlwAccountRequest;
import com.fintech.app.request.LocalTransferRequest;
import com.fintech.app.request.TransferRequest;
import com.fintech.app.request.VerifyTransferRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.FlwAccountResponse;
import com.fintech.app.response.OtherBankTransferResponse;
import com.fintech.app.response.VerifyTransferResponse;
import com.fintech.app.service.LocalTransferService;
import com.fintech.app.service.OtherBankTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final OtherBankTransferService otherBankTransferService;

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
         return otherBankTransferService.getBanks();
    }

    @PostMapping("/resolveOtherBank")
    public BaseResponse<FlwAccountResponse> resolveOtherAccount(@RequestBody FlwAccountRequest flwAccountRequest){
        return otherBankTransferService.resolveAccount(flwAccountRequest);
    }

    @PostMapping("/otherBank")
    public BaseResponse<OtherBankTransferResponse> processTransfer(@Valid @RequestBody TransferRequest transferRequest){
        return otherBankTransferService.initiateOtherBankTransfer(transferRequest);
    }

    @PostMapping("/verify")
    public BaseResponse<VerifyTransferResponse> verifyTransactions(@RequestBody VerifyTransferRequest verifyTransferRequest){
        return otherBankTransferService.verifyTransaction(verifyTransferRequest);
    }

}
