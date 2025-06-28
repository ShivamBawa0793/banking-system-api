package com.example.bankingsystem.repository.impl;

import com.example.bankingsystem.model.Account;
import com.example.bankingsystem.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    // Initialize a logger for this class
    private static final Logger logger = LoggerFactory.getLogger(AccountRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Account> accountRowMapper = (rs, rowNum) -> new Account(
            rs.getString("id"),
            rs.getInt("creation_timestamp"),
            rs.getInt("balance"),
            rs.getInt("total_outgoing")
    );

    @Autowired
    public AccountRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("AccountRepositoryImpl initialized with JdbcTemplate.");
    }

    @Override
    public Boolean save(int timeStamp, String accountId) {
        logger.info("Attempting to save new account with ID: {}", accountId);
        String sql = "INSERT INTO ACCOUNTS(id, creation_timestamp) VALUES (?,?)";
        try {
            int rowsAffected = jdbcTemplate.update(sql, accountId, timeStamp);
            if (rowsAffected > 0) {
                logger.info("Account {} saved successfully. Rows affected: {}", accountId, rowsAffected);
            } else {
                logger.warn("Account {} save operation completed but no rows were affected. Account might already exist.", accountId);
            }
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            logger.error("Failed to save account {}. Data access error: {}", accountId, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("An unexpected error occurred while saving account {}: {}", accountId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Boolean updateBalance(int timeStamp, String accountId, int amount) {
        logger.info("Attempting to update balance for account ID: {} with amount: {}", accountId, amount);
        String sql = "UPDATE ACCOUNTS SET balance = balance + ?, creation_timestamp = ? where id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, amount, timeStamp, accountId);
            if (rowsAffected > 0) {
                logger.info("Balance updated successfully for account ID {}. Rows affected: {}", accountId, rowsAffected);
            } else {
                logger.warn("Balance update for account ID {} completed but no rows were affected. Account might not exist.", accountId);
            }
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            logger.error("Failed to update balance for account ID {}. Data access error: {}", accountId, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("An unexpected error occurred while updating balance for account ID {}: {}", accountId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Optional<Integer> getBalance(String accountId) {
        logger.info("Attempting to retrieve balance for account ID: {}", accountId);
        String sql = "SELECT balance FROM ACCOUNTS where id = ?";
        try {
            Integer balance = jdbcTemplate.queryForObject(sql, Integer.class, accountId);
            logger.debug("Retrieved balance {} for account ID {}.", balance, accountId);
            return Optional.ofNullable(balance);
        } catch (DataAccessException e) {
            // This usually means no rows found or multiple rows found for queryForObject
            logger.warn("Balance not found or data access issue for account ID {}: {}", accountId, e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            logger.error("An unexpected error occurred while getting balance for account ID {}: {}", accountId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Integer> transfer(int timeStamp, String sourceAccountId, String targetAccountId, int amount) {
        // Note: This 'transfer' method in the repository seems to only handle the target account update (deposit part).
        // The withdrawal from source is handled by the 'withdraw' method.
        // It's good practice for repository methods to be atomic operations (single DB action).
        logger.info("Attempting to transfer (deposit part) amount {} to target account ID: {}", amount, targetAccountId);
        String sql = "UPDATE ACCOUNTS SET balance = balance + ? , creation_timestamp = ? where id = ?";
        try {
            int rowAffected = jdbcTemplate.update(sql, amount, timeStamp, targetAccountId);
            if (rowAffected > 0) {
                logger.info("Transfer (deposit part) successful for target account {}. Rows affected: {}.", targetAccountId, rowAffected);
            } else {
                logger.warn("Transfer (deposit part) for target account {} completed but no rows affected. Account might not exist.", targetAccountId);
            }
            return Optional.ofNullable(rowAffected);
        } catch (DataAccessException e) {
            logger.error("Failed to transfer (deposit part) to target account {}. Data access error: {}", targetAccountId, e.getMessage(), e);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("An unexpected error occurred during transfer (deposit part) to target account {}: {}", targetAccountId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Account> findById(String accountId) {
        logger.info("Attempting to find account by ID: {}", accountId);
        String sql = "SELECT id, creation_timestamp, balance, total_outgoing FROM ACCOUNTS WHERE id = ?";
        try {
            Account account = jdbcTemplate.queryForObject(sql, accountRowMapper, accountId);
            logger.debug("Found account by ID {}: {}", accountId, account);
            return Optional.ofNullable(account);
        } catch (DataAccessException e) {
            // This typically means no account found for the given ID
            logger.debug("Account with ID {} not found in database or data access issue: {}", accountId, e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            logger.error("An unexpected error occurred while finding account by ID {}: {}", accountId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Boolean withdraw(int timeStamp, String accountId, int sourceBalance, int withdrawAmount) {
        logger.info("Attempting withdrawal from account ID: {} for amount: {}. Current balance: {}", accountId, withdrawAmount, sourceBalance);
        int updatedBalance = sourceBalance - withdrawAmount;
        String sql = "UPDATE ACCOUNTS SET balance = ?, creation_timestamp = ?, total_outgoing = total_outgoing + ? where id = ?"; // Added total_outgoing update
        try {
            int rowsAffected = jdbcTemplate.update(sql, updatedBalance, timeStamp, withdrawAmount, accountId); // Added withdrawAmount to update
            if (rowsAffected > 0) {
                logger.info("Withdrawal successful from account ID {}. New balance: {}. Rows affected: {}", accountId, updatedBalance, rowsAffected);
            } else {
                logger.warn("Withdrawal from account ID {} completed but no rows affected. Account might not exist.", accountId);
            }
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            logger.error("Failed to withdraw from account ID {}. Data access error: {}", accountId, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("An unexpected error occurred while withdrawing from account ID {}: {}", accountId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Account> getAllAccounts() {
        logger.info("Attempting to retrieve all accounts from the database.");
        String sql = "SELECT id, creation_timestamp, balance, total_outgoing from ACCOUNTS";
        try {
            List<Account> accounts = jdbcTemplate.query(sql, accountRowMapper);
            logger.debug("Retrieved {} accounts from database.", accounts.size());
            return accounts;
        } catch (DataAccessException e) {
            logger.error("Failed to retrieve all accounts from database. Data access error: {}", e.getMessage(), e);
            return List.of(); // Return empty list on error
        } catch (Exception e) {
            logger.error("An unexpected error occurred while retrieving all accounts: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
