package com.bank.cms.controller;

import com.bank.cms.api.response.ApiResponse;
import com.bank.cms.dto.request.CreateAccountRequest;
import com.bank.cms.dto.request.UpdateAccountRequest;
import com.bank.cms.dto.response.AccountResponse;
import com.bank.cms.entity.Account;
import com.bank.cms.service.AccountService;
import com.bank.cms.service.impl.AccountServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account Service", description = "APIs related to account management")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    private AccountServiceImpl accountServiceImpl;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Create a new bank account")
    @PostMapping
    public ApiResponse<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {

        Account account = accountService.createAccount(request);

        AccountResponse response = new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getCustomerName(),
                account.getBalance(),
                account.getEmailId(),
                account.getMobileNumber(),
                account.getAddress()
        );

        return new ApiResponse<>(
                "SUCCESS",
                "Account created successfully",
                response
        );
    }

    @Operation(summary = "Get account by ID")
    @GetMapping("/{id}")
    public ApiResponse<AccountResponse> getAccountById(@PathVariable Long id) {

        Account account = accountService.getAccountById(id);

        AccountResponse response = new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getCustomerName(),
                account.getBalance(),
                account.getEmailId(),
                account.getMobileNumber(),
                account.getAddress()
        );

        return new ApiResponse<>(
                "SUCCESS",
                "Account fetched successfully",
                response
        );
    }


    @Operation(summary = "Get account by Account Number")
    @GetMapping("/accountNumber")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountByAcctNum(@RequestParam String accountNumber) {
        AccountResponse account = accountServiceImpl.findByAccountNumber(accountNumber);
        ApiResponse<AccountResponse> response = new ApiResponse<>("Success","Account fetched successfully" , account);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Amend account details by accountNumber")
    @PutMapping("/{accountNumber}")
    public ApiResponse<AccountResponse> amendAccountByAccountNumber(@PathVariable String accountNumber,@Valid @RequestBody UpdateAccountRequest request){
        AccountResponse account = accountServiceImpl.amendAccountByAccountNumber(accountNumber,request);

        AccountResponse response = new AccountResponse(
                account.getAccountId(),
                account.getAccountNumber(),
                account.getCustomerName(),
                account.getBalance(),
                account.getEmailId(),
                account.getMobileNumber(),
                account.getAddress()
        );


        return new ApiResponse<>(
                "Success",
                "Account updated successfully",
                response
        );

    }
}
