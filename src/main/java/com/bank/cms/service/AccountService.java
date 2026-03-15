package com.bank.cms.service;

import com.bank.cms.dto.request.CreateAccountRequest;
import com.bank.cms.entity.Account;

public interface AccountService {

    Account createAccount(CreateAccountRequest request);

    Account getAccountById(Long id);

//   AccountResponse findByAccountNumber(String accountNumber);

}

