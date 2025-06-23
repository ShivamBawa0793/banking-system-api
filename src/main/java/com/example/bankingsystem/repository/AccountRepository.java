package com.example.bankingsystem.repository;


import com.example.bankingsystem.model.Account;

import java.util.Optional;

public interface AccountRepository {
    /**
     * Saves a new account to the database.
     * @param accountId The Account_ID to save.
     * @return true if the account id was saved, false if an account with the same ID already exists.
     */
    Boolean save(int timeStamp, String accountId);

    /**
     * Updates the balance of an existing account.
     * @param timeStamp the update of timeStamp while balance update.
     * @param accountId The ID of the account to update.
     * @param amount The amount to add to the balance (can be negative for withdrawal).
     * @return true if the balance was updated, false if the account was not found.
     */
    Boolean updateBalance(int timeStamp, String accountId, int amount);

    /**
     * Returns the current balance of an account.
     * @param accountId The ID of the account.
     * @return An Optional containing the balance if found, otherwise empty.
     */
    Optional<Integer> getBalance(String accountId);

    /**
     * Transfers a given amount of money from a source account to a target account.
     * The description in the provided image referred to a transfer operation.
     *
     * @param sourceAccountId The identifier of the account from which money is transferred.
     * @param targetAccountId The identifier of the account to which money is transferred.
     * @param amount The amount of money to transfer. Must be greater than 0.
     * @return An Optional containing the *new balance of the source account* if the transfer was successful.
     * Returns Optional.empty() otherwise under the following conditions:
     */
    Optional<Integer> transfer(int timeStamp, String sourceAccountId, String targetAccountId, int amount);

    /**
     * Finds an account by its ID.
     * @param accountId The ID of the account.
     * @return An Optional containing the Account if found, otherwise empty.
     */
    Optional<Account> findById(String accountId);

}
