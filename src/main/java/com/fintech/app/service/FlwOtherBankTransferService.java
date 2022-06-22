package com.fintech.app.service;

import com.fintech.app.model.User;
import com.fintech.app.response.FlwAccountResponse;
import com.fintech.app.model.FlwBank;
import com.fintech.app.request.FlwAccountRequest;

import java.util.List;

public interface FlwOtherBankTransferService {
    List<FlwBank> getBanks(String currency);
    FlwAccountResponse resolveAccount(FlwAccountRequest flwAccountRequest);
}
