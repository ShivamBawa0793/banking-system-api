package com.example.bankingsystem.dto;

public class DepositRequest {
    private String accountId;
    private int amount;

    //no arg-constructor
    public DepositRequest(){};

    //args-constructor
    public DepositRequest(int amount, String accountId) {
        this.amount = amount;
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
