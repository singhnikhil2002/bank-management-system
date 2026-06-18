package com.bank.cms.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private String accountNumber;
    private String email;
    private String mobileNumber;
    private Double amount;
    private String type;        // DEBIT, CREDIT
    private String message;
}