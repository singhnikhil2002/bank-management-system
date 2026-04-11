package com.bank.cms.service;

import com.bank.cms.dto.request.TransactionRequest;
import com.bank.cms.entity.Transaction;

import java.util.List;

public interface TransactionService {
    String transferMoney(TransactionRequest request);
    List<Transaction> getTransaction(String fromAccount,String toAccount);
}
