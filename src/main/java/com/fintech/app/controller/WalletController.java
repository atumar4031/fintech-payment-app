package com.fintech.app.controller;

import com.fintech.app.request.WalletRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.WalletResponse;
import com.fintech.app.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping
@CrossOrigin("*")
public class WalletController {
    private final WalletService walletService;

//    @PostMapping("/wallet")
//    public BaseResponse<WalletResponse> createWallet(@RequestBody WalletRequest walletRequest){
//        return walletService.createWallet(walletRequest);
//    }
}
