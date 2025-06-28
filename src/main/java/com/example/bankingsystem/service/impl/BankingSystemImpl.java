package com.example.bankingsystem.service.impl;

import com.example.bankingsystem.model.Account;
import com.example.bankingsystem.repository.AccountRepository;
import com.example.bankingsystem.service.BankingSystem;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class BankingSystemImpl implements BankingSystem {

    // Initialize a logger for this class
    private static final Logger logger = LoggerFactory.getLogger(BankingSystemImpl.class);

    private final AccountRepository accountRepository;

    public BankingSystemImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        logger.info("BankingSystemImpl initialized with AccountRepository.");
    }

    @Override
    public boolean createAccount(int timeStamp, String accountId) {
        logger.info("Attempting to create account with ID: {}", accountId);
        if(accountId == null || accountId.isEmpty()){
            logger.warn("Account creation failed: Invalid account ID provided (null or empty).");
            return false;
        }
        boolean created = accountRepository.save(timeStamp, accountId);
        if (created) {
            logger.info("Account {} created successfully.", accountId);
        } else {
            logger.warn("Account {} creation failed: Account might already exist or repository issue.", accountId);
        }
        return created;
    }

    @Override
    public Optional<Integer> deposit(int timestamp, String accountId, int amount) {
        logger.info("Attempting deposit for account ID: {}, Amount: {}", accountId, amount);
        if(accountId == null || accountId.isEmpty() || amount<=0){
            logger.warn("Deposit failed for account ID {}: Invalid account ID (null/empty) or amount ({}).", accountId, amount);
            return Optional.empty();
        }
        Boolean balanceUpdated =  accountRepository.updateBalance(timestamp, accountId, amount);

        if(balanceUpdated){
            Optional<Integer> newBalance = accountRepository.getBalance(accountId);
            if (newBalance.isPresent()) {
                logger.info("Deposit successful for account ID {}. New balance: {}.", accountId, newBalance.get());
            } else {
                logger.error("Deposit successful but could not retrieve new balance for account ID {}.", accountId);
            }
            return newBalance;
        } else {
            logger.warn("Deposit failed for account ID {}: Repository could not update balance. Account not found or other issue.", accountId);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> transfer(int timestamp, String sourceAccountId, String targetAccountId, int amount) {
        logger.info("Attempting transfer from source: {} to target: {}, Amount: {}", sourceAccountId, targetAccountId, amount);

        // 1. Validate request parameters.
        if(sourceAccountId == null || sourceAccountId.isEmpty() || amount <= 0
                || targetAccountId == null || targetAccountId.isEmpty()
                || sourceAccountId.equalsIgnoreCase(targetAccountId) ){
            logger.warn("Transfer failed: Invalid request parameters. Source: {}, Target: {}, Amount: {}. Reason: null/empty IDs, invalid amount, or source equals target.", sourceAccountId, targetAccountId, amount);
            return Optional.empty();
        }

        // 2. Validate if account id is present in DB
        Optional<Account> sourceAccountOpt = accountRepository.findById(sourceAccountId);
        if(sourceAccountOpt.isEmpty()){
            logger.warn("Transfer failed: Source account ID {} not found.", sourceAccountId);
            return Optional.empty();
        }
        Optional<Account> targetAccountOpt = accountRepository.findById(targetAccountId);
        if(targetAccountOpt.isEmpty()){
            logger.warn("Transfer failed: Target account ID {} not found.", targetAccountId);
            return Optional.empty();
        }

        // 3. Validate if source has sufficient balance for transfer.
        Optional<Integer> sourceAccountBalance = accountRepository.getBalance(sourceAccountId);
        if(sourceAccountBalance.isEmpty() || sourceAccountBalance.get() < amount){
            logger.warn("Transfer failed for source {}: Insufficient balance ({}) or balance not found for amount {}.", sourceAccountId, sourceAccountBalance.orElse(0), amount);
            return Optional.empty();
        }

        // 4. Withdraw from source account.
        logger.debug("Attempting withdrawal from source account {} for amount {}.", sourceAccountId, amount);
        Boolean updateSourceBalance = accountRepository.withdraw(timestamp, sourceAccountId,
                sourceAccountBalance.get(), amount);

        if(updateSourceBalance){
            logger.info("Withdrawal successful from source account {}.", sourceAccountId);
            // 5. Transfer from source account to target account
            logger.debug("Attempting transfer to target account {} for amount {}.", targetAccountId, amount);
            Optional<Integer> transferResult = accountRepository.transfer(timestamp, sourceAccountId, targetAccountId, amount);
            if(transferResult.isPresent()){
                logger.info("Transfer successful to target account {}.", targetAccountId);
                // Return new balance of the source account after successful transfer
                return accountRepository.getBalance(sourceAccountId);
            } else {
                logger.error("Transfer operation failed at repository level for source: {} to target: {}. Amount: {}. This indicates a potential data inconsistency after withdrawal.", sourceAccountId, targetAccountId, amount);
                // In a real system, you'd likely need to roll back the withdrawal here.
            }
        } else {
            logger.error("Withdrawal from source account {} failed at repository level.", sourceAccountId);
        }
        return Optional.empty();
    }

    @Override
    public List<String> topSpenders(int timestamp, int n) {
        logger.info("Received request for top {} spenders at timestamp {}. (Method not fully implemented yet)", n, timestamp);
        // This method needs full implementation in the repository and service.
        return List.of();
    }

    @Override
    public Optional<Account> accountById(String accountId) {
        logger.info("Attempting to find account by ID: {}", accountId);
        if(accountId == null || accountId.isEmpty()){
            logger.warn("Attempt to find account failed: Invalid account ID (null or empty).");
            return Optional.empty();
        }
        Optional<Account> account = accountRepository.findById(accountId);
        if(account.isPresent()){
            logger.debug("Account with ID {} found: {}.", accountId, account.get());
        } else {
            logger.debug("Account with ID {} not found in repository.", accountId);
        }
        return account;
    }

    @Override
    public List<Account> getAllAccounts() {
        logger.info("Attempting to retrieve all accounts from repository.");
        List<Account> accounts = accountRepository.getAllAccounts();
        logger.debug("Retrieved {} accounts.", accounts.size());
        return accounts;
    }

    @Override
    public Optional<Integer> getBalance(String accountId) {
        logger.info("Attempting to get balance for account ID: {}", accountId);
        if(accountId == null || accountId.isEmpty()){
            logger.warn("Attempt to get balance failed: Invalid account ID (null or empty).");
            return Optional.empty();
        }
        Optional<Integer> balance = accountRepository.getBalance(accountId);
        if(balance.isPresent()){
            logger.debug("Balance for account ID {} is {}.", accountId, balance.get());
        } else {
            logger.debug("Balance not found for account ID {}. Account may not exist.", accountId);
        }
        return balance;
    }
}
