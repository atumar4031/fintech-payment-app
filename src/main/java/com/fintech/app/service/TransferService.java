package com.fintech.app.service;

import com.fintech.app.request.TransferRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.OtherBankTransferResponse;
import com.fintech.app.response.TransferResponse;

import java.util.List;

public interface TransferService {
    BaseResponse<OtherBankTransferResponse> initiateOtherBankTransfer(TransferRequest transferRequest);
}
