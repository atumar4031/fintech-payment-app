package com.fintech.app.service;

import com.fintech.app.model.Wallet;
import com.fintech.app.request.FlwWalletRequest;
import org.springframework.boot.configurationprocessor.json.JSONException;

public interface WalletService {
    Wallet createWallet(FlwWalletRequest walletRequest) throws JSONException;
}
