package com.fintech.app.service;


import com.fintech.app.model.LocalTransfer;
import com.fintech.app.request.LocalTransferRequest;
import com.fintech.app.response.BaseResponse;

public interface LocalTransferService {
    BaseResponse<LocalTransfer> makeLocalTransfer(LocalTransferRequest transferRequest);
    BaseResponse<String> resolveLocalAccount(String accountNumber);
}
