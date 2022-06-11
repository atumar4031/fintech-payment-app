package com.fintech.app.service.impl;

import com.fintech.app.request.WalletRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.WalletResponse;
import com.fintech.app.service.WalletService;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl implements WalletService {
    @Override
    public BaseResponse<WalletResponse> createWallet(WalletRequest walletRequest) {
        return null;
    }
}
