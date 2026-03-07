package com.bank.cms.service;

import com.bank.cms.dto.request.CreateAccountRequest;
import com.bank.cms.dto.response.AccountResponse;
import com.bank.cms.entity.Account;
import com.bank.cms.exception.ResourceNotFoundException;
import com.bank.cms.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;

public interface AccountService {

    Account createAccount(CreateAccountRequest request);

    Account getAccountById(Long id);

//   AccountResponse findByAccountNumber(String accountNumber);

}

