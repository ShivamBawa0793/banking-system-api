package com.example.bankingsystem.service.impl;

import com.example.bankingsystem.service.BankingSystem;

import java.util.List;
import java.util.Optional;

public class BankingSystemImpl implements BankingSystem {
    @Override
    public boolean createAccount(int timestamp, String accountId) {
        return false;
    }

    @Override
    public Optional<Integer> deposit(int timestamp, String accountId, int amount) {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> transfer(int timestamp, String sourceAccountId, String targetAccountId, int amount) {
        return Optional.empty();
    }

    @Override
    public List<String> topSpenders(int timestamp, int n) {
        return List.of();
    }
}
