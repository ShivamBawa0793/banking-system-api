package com.example.bankingsystem.model;

import java.util.Map;

public class AccountManager {
    private Map<String, Account> accounts;

    public AccountManager(Map<String, Account> accounts) {
        this.accounts = accounts;
    }
}
