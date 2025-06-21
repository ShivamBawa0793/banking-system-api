package com.example.bankingsystem.model;

public class Account {
    private String accountId;
    private int timestamp;
    private int balance;
    private int totalOutgoing;

    public Account(String accountId, int balance, int timestamp, int totalOutgoing) {
        this.accountId = accountId;
        this.balance = balance;
        this.timestamp = timestamp;
        this.totalOutgoing = totalOutgoing;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getTotalOutgoing() {
        return totalOutgoing;
    }

    public void setTotalOutgoing(int totalOutgoing) {
        this.totalOutgoing = totalOutgoing;
    }
}
