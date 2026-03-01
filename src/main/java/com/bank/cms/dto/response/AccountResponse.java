package com.bank.cms.dto.response;

public class AccountResponse {

    private Long accountId;
    private String accountNumber;
    private String customerName;
    private Double balance;

    public AccountResponse(Long accountId, String accountNumber,
                           String customerName, Double balance) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.balance = balance;
    }

    // getters only
    public Long getAccountId() { return accountId; }
    public String getAccountNumber() { return accountNumber; }
    public String getCustomerName() { return customerName; }
    public Double getBalance() { return balance; }
}
