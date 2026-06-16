package com.bank.cms.service.impl;

import com.bank.cms.dto.request.TransactionRequest;
import com.bank.cms.entity.Account;
import com.bank.cms.entity.Transaction;
import com.bank.cms.exception.ResourceNotFoundException;
import com.bank.cms.kafka.TransactionEvent;
import com.bank.cms.kafka.TransactionProducer;
import com.bank.cms.repository.AccountRepository;
import com.bank.cms.repository.TransactionRepository;
import com.bank.cms.service.RedisService;
import com.bank.cms.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor                          // ✅ replaces all @Autowired
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final RedisService redisService;      // ✅ properly injected now
    private final TransactionProducer transactionProducer;

    @Override
    @Transactional
    public String transferMoney(TransactionRequest request) {

        // 1. Fetch Accounts
        Account sender = accountRepository.findByAccountNumber(request.getFromAccount())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        Account receiver = accountRepository.findByAccountNumber(request.getToAccount())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        // 2. Same account check
        if (request.getFromAccount().equals(request.getToAccount())) {
            throw new ResourceNotFoundException("Both accounts are same");
        }

        // 3. Balance check
        if (sender.getBalance() < request.getAmount()) {
            throw new ResourceNotFoundException("Insufficient balance");
        }

        // 4. Debit sender
        sender.setBalance(sender.getBalance() - request.getAmount());
        accountRepository.save(sender);
        redisService.evictBalanceCache(request.getFromAccount()); // ✅ inside method now

        // 5. Credit receiver
        receiver.setBalance(receiver.getBalance() + request.getAmount());
        accountRepository.save(receiver);
        redisService.evictBalanceCache(request.getToAccount());   // ✅ inside method now

        // 6. Save transaction record
        Transaction txn = new Transaction();
        txn.setFromAccount(request.getFromAccount());
        txn.setToAccount(request.getToAccount());
        txn.setAmount(request.getAmount());
        txn.setStatus("SUCCESS");
        txn.setType("TRANSFER");
        txn.setLocalDateTime(LocalDateTime.now());
        Transaction savedTxn = transactionRepository.save(txn);

        TransactionEvent event = new TransactionEvent(
                savedTxn.getId().toString(),       // eventId
                request.getFromAccount(),     // fromAccount
                request.getToAccount(),       // toAccount
                request.getAmount(),          // amount
                "SUCCESS",                    // status
                "TRANSFER",                   // type
                LocalDateTime.now()           // timestamp
        );
        transactionProducer.publishTransactionEvent(event);  // ✅ fire and forget

        return "Transaction Successful";
    }

    @Override
    public List<Transaction> getTransaction(String fromAccount, String toAccount) {

        Account sender = accountRepository.findByAccountNumber(fromAccount)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        Account receiver = accountRepository.findByAccountNumber(toAccount)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        if (fromAccount.equals(toAccount)) {
            throw new ResourceNotFoundException("Both accounts are same");
        }

        List<Transaction> txnDetails = transactionRepository
                .findByFromAccountOrToAccount(fromAccount, toAccount);

        if (txnDetails.isEmpty()) {
            throw new ResourceNotFoundException("No transactions found");
        }

        return txnDetails;
    }
}