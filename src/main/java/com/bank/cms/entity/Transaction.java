package com.bank.cms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "acctTxn")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String fromAccount;
    private String toAccount;
    private double amount;
    private String type;
    private String status;
    private LocalDateTime localDateTime;
}
