package com.bank.cms.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {

    private String eventId;           // unique ID for this event
    private String fromAccount;       // sender account
    private String toAccount;         // receiver account
    private Double amount;            // transfer amount
    private String status;            // SUCCESS / FAILED
    private String type;              // TRANSFER / DEPOSIT / WITHDRAWAL
    private LocalDateTime timestamp;  // when it happened

}