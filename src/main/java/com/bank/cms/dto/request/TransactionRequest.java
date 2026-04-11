package com.bank.cms.dto.request;


import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TransactionRequest {
    private String fromAccount;
    private String toAccount;
    private double amount;
}
