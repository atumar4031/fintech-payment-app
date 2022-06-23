package com.fintech.app.service;

import com.fintech.app.request.FLWTransferRequest;
import com.fintech.app.request.TransferRequest;
import com.fintech.app.response.BaseResponse;
import com.fintech.app.response.TransferResponse;

import java.util.List;

public interface TransferService {
    BaseResponse<String> initiateOtherbankTransfer(TransferRequest transferRequest);
    BaseResponse<TransferResponse> findTransfer(long id);
    BaseResponse<List<TransferResponse>> findAllTransfers();

}
