package com.fintech.app.service;

import com.fintech.app.request.WalletRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.WalletResponse;

public interface WalletService {
    BaseResponse<WalletResponse> createWallet(WalletRequest walletRequest);
}
