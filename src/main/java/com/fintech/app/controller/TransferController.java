package com.fintech.app.controller;

import com.fintech.app.model.LocalTransfer;
import com.fintech.app.request.LocalTransferRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.service.LocalTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfers")
public class TransferController {
    private final LocalTransferService localTransferService;
    @PostMapping("/local")
    public BaseResponse<LocalTransfer> makeLocalTransfer(@RequestBody LocalTransferRequest localTransferRequest){
        return localTransferService.makeLocalTransfer(localTransferRequest);
    }
    @GetMapping("/resolveLocalAccount/{accountNumber}")
    public BaseResponse<String> resolveLocalAccount(@PathVariable String accountNumber){
        return localTransferService.resolveLocalAccount(accountNumber);
    }
}
