package com.example.bankingsystem.dto.response;

public class AccountDetailsResponse {
    private String accountId;
    private int timeStamp;
    private int balance;
    private int totalOutgoing;

    public AccountDetailsResponse(String accountId, int balance, int timeStamp, int totalOutgoing) {
        this.accountId = accountId;
        this.balance = balance;
        this.timeStamp = timeStamp;
        this.totalOutgoing = totalOutgoing;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getTotalOutgoing() {
        return totalOutgoing;
    }

    public void setTotalOutgoing(int totalOutgoing) {
        this.totalOutgoing = totalOutgoing;
    }
}
