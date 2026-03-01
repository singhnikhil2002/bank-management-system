package com.bank.cms.service.impl;

import com.bank.cms.dto.request.CreateAccountRequest;
import com.bank.cms.exception.ResourceNotFoundException;
import com.bank.cms.service.AccountService;
import org.springframework.stereotype.Service;
import com.bank.cms.entity.Account;
import com.bank.cms.repository.AccountRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;




@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account createAccount(CreateAccountRequest request) {

        Account account = new Account();
        account.setCustomerName(request.getCustomerName());
        account.setBalance(request.getInitialBalance());
        account.setAccountNumber(generateAccountNumber());

        return accountRepository.save(account);
    }


    @Override
    public Account getAccountById(Long id) {

        return accountRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found with id: " + id)
                );
    }

    private String generateAccountNumber() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return now.format(dtf);
    }
}
