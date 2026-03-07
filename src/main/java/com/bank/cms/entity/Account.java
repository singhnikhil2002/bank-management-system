package com.bank.cms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String customerName;

    private Double balance;

    // getters & setters

    public Long getId(){
        return Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }

    public String getAccountNumber(){
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerName(){
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Double getBalance(){
        return balance;
    }
    public void setBalance(Double balance) {
        this.balance = balance;
    }

}
