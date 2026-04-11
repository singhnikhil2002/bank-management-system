package com.bank.cms.service.impl;

import com.bank.cms.dto.request.TransactionRequest;
import com.bank.cms.entity.Account;
import com.bank.cms.entity.Transaction;
import com.bank.cms.repository.AccountRepository;
import com.bank.cms.repository.TransactionRepository;
import com.bank.cms.service.TransactionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    @Transactional
    public String transferMoney(TransactionRequest request){
        // 1. Fetch Account
        Account sender = accountRepository.findByAccountNumber(request.getFromAccount()).orElseThrow(
                () -> new RuntimeException("Sender not found")
        );
        Account receiver = accountRepository.findByAccountNumber(request.getToAccount()).orElseThrow(
                () -> new RuntimeException("Receiver not found")
        );

        if(sender == null || receiver == null){
            throw new RuntimeException("Invalid Account Number");
        }

        // 2.Same Account check
        if(request.getFromAccount().equals(request.getToAccount())){
            throw new RuntimeException("Both Accounts are same");
        }

        // 3. Balance Check
        if(sender.getBalance() < request.getAmount()){
            throw new RuntimeException("Insufficient Balance");
        }

        // 4. Debit
        sender.setBalance(sender.getBalance() - request.getAmount());
        accountRepository.save(sender);

        // 5. Credit
        receiver.setBalance(receiver.getBalance() + request.getAmount());
        accountRepository.save(receiver);

        // 6. Transaction save
        Transaction txn = new Transaction();
        txn.setFromAccount(request.getFromAccount());
        txn.setToAccount(request.getToAccount());
        txn.setAmount(request.getAmount());
        txn.setStatus("Success");
        txn.setType("Transfer");
        txn.setLocalDateTime(LocalDateTime.now());
        transactionRepository.save(txn);

        return "Transaction Successful";

    }

    @Override
    public List<Transaction> getTransaction(String fromAccount,String toAccount){
        List<Transaction> txnDetails = transactionRepository.findByFromAccountOrToAccount(fromAccount,toAccount);

        return txnDetails;
    }

}
