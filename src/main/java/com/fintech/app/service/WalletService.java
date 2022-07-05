package com.fintech.app.service;

import com.fintech.app.model.User;
import com.fintech.app.model.Wallet;
import com.fintech.app.request.FlwWalletRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.WalletResponse;
import org.springframework.boot.configurationprocessor.json.JSONException;

public interface WalletService {
    Wallet createWallet(FlwWalletRequest walletRequest) throws JSONException;
    BaseResponse<WalletResponse> fetchUserWallet(User user);
}
