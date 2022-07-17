package com.fintech.app.controller;

import com.fintech.app.request.FundWalletRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.WalletResponse;
import com.fintech.app.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/wallet")
public class WalletController {
    private final WalletService walletService;

    @GetMapping()
    public BaseResponse<WalletResponse> fetchUserWallet() {
        return walletService.fetchUserWallet();
    }

    @PostMapping("fund")
    public BaseResponse<?> fundWallet(@RequestBody FundWalletRequest request) {
        try {
            return walletService.fundWallet(request);
        }catch (Exception e) {
            return new BaseResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }
}
