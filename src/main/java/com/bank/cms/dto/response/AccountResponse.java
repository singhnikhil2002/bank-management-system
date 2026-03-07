package com.bank.cms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AccountResponse {

    private Long accountId;
    private String accountNumber;
    private String customerName;
    private Double balance;

    public AccountResponse() {

    }
}
