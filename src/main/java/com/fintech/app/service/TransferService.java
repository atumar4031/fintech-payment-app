package com.fintech.app.service;

import com.fintech.app.request.FLWTransferRequest;
import com.fintech.app.response.TransferResponse;

import java.util.List;

public interface TransferService {
    String FlwInitiateTransfer(long userId, FLWTransferRequest transferRequest);
    TransferResponse findTransfer(long id);
    List<TransferResponse> findAllTransfers();

}
