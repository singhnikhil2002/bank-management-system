package com.bank.cms.service.impl;

import com.bank.cms.dto.request.CreateAccountRequest;
import com.bank.cms.dto.request.UpdateAccountRequest;
import com.bank.cms.dto.response.AccountResponse;
import com.bank.cms.exception.ResourceNotFoundException;
import com.bank.cms.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
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
        account.setEmailId(request.getEmailId());
        account.setMobileNumber(request.getMobileNumber());
        account.setAddress(request.getAddress());

        return accountRepository.save(account);
    }


    @Override
    public Account getAccountById(Long id) {

        return accountRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found with id: " + id)
                );
    }

    public AccountResponse findByAccountNumber(String accountNumber){
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found with this number : " + accountNumber)
                );
        return mapAccount(account);
    }

    public AccountResponse amendAccountByAccountNumber(String accountNumber, UpdateAccountRequest req){
        Account account = accountRepository.findByAccountNumber(accountNumber).
                orElseThrow(() ->
                            new ResourceNotFoundException("Invalid account :" + accountNumber)
                        );
        account.setCustomerName(req.getCustomerName());
        account.setEmailId(req.getEmailId());
        account.setMobileNumber(req.getMobileNumber());
        account.setAddress(req.getAddress());
        accountRepository.save(account);

        return mapAccount(account);
    }

    public AccountResponse mapAccount(Account account){
        AccountResponse dto = new AccountResponse();
        dto.setAccountId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBalance(account.getBalance());
        dto.setCustomerName(account.getCustomerName());
        dto.setEmailId(account.getEmailId());
        dto.setMobileNumber(account.getMobileNumber());
        dto.setAddress(account.getAddress());
        return dto;
    }
    private String generateAccountNumber() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return now.format(dtf);
    }
}
