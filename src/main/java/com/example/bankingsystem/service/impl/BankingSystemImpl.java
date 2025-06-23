package com.example.bankingsystem.service.impl;

import com.example.bankingsystem.repository.AccountRepository;
import com.example.bankingsystem.service.BankingSystem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankingSystemImpl implements BankingSystem {

    private final AccountRepository accountRepository;

    public BankingSystemImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public boolean createAccount(int timeStamp, String accountId) {
        if(accountId == null || accountId.isEmpty()){
            return false;
        }
        return accountRepository.save(timeStamp, accountId);
    }

    @Override
    public Optional<Integer> deposit(int timestamp, String accountId, int amount) {
        if(accountId == null || accountId.isEmpty() || amount<=0){
            return Optional.empty();
        }
        Boolean balanceUpadted =  accountRepository.updateBalance(timestamp,accountId, amount);

        if(balanceUpadted){
            return accountRepository.getBalance(accountId);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> transfer(int timestamp, String sourceAccountId, String targetAccountId, int amount) {
        if(sourceAccountId == null || sourceAccountId.isEmpty() || amount<=0
        || targetAccountId == null || targetAccountId.isEmpty()
                || sourceAccountId.equalsIgnoreCase(targetAccountId) ){
            return Optional.empty();
        }
        Optional<Integer> sourceAccountBalance = accountRepository.getBalance(sourceAccountId);
        if(sourceAccountBalance.get() < amount){
            return Optional.empty();
        }
        Optional<Integer> transfer = accountRepository.transfer(timestamp,sourceAccountId,targetAccountId,amount);
        if(transfer.isPresent()){
            return accountRepository.getBalance(sourceAccountId);
        }
        return Optional.empty();
    }

    @Override
    public List<String> topSpenders(int timestamp, int n) {
        return List.of();
    }
}
