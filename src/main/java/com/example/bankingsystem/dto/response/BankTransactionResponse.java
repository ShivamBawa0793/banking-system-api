package com.example.bankingsystem.dto.response;

public class BankTransactionResponse extends ApiResponse {

    private String accountId;
    private Integer newBalance;

    public BankTransactionResponse(String message, Boolean success, String accountId, Integer newBalance) {
        super(message, success);
        this.accountId = accountId;
        this.newBalance = newBalance;
    }

    public BankTransactionResponse(String errorCode, String message, Boolean success) {
        super(errorCode, message, success);
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Integer getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(Integer newBalance) {
        this.newBalance = newBalance;
    }
}
