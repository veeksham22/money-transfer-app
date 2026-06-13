package com.progressive.banking.moneytransfer.service;

import java.util.List;

import com.progressive.banking.moneytransfer.domain.dto.AccountResponse;
import com.progressive.banking.moneytransfer.domain.dto.BalanceResponse;
import com.progressive.banking.moneytransfer.domain.dto.TransferResponse;

public interface AccountService {

    AccountResponse getAccount(Integer id, String username);

    BalanceResponse getBalance(Integer id, String username);

    List<TransferResponse> getTransactions(Integer id, String username);
    
    Integer getAccountIdByHolderName(String holderName);
}