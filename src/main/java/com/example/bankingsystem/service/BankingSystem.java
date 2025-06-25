package com.example.bankingsystem.service;

import com.example.bankingsystem.model.Account;

import java.util.List;
import java.util.Optional;

public interface BankingSystem {
    /**
     * Creates a new account with the given identifier if it doesn't already exist.
     *
     * @param timestamp The timestamp when the account creation request was made (Unix seconds).
     * @param accountId The unique identifier for the account.
     * @return true if the account was successfully created, false if an account
     * with the given accountId already exists or if the accountId is invalid (null or empty).
     */
    boolean createAccount(int timestamp, String accountId);

    /**
     * Deposits a given amount of money into a specified account.
     *
     * @param timestamp The timestamp of the deposit operation.
     * @param accountId The identifier of the account to deposit into.
     * @param amount The amount of money to deposit. Must be greater than 0.
     * @return An Optional containing the new balance of the account if the deposit was successful.
     * Returns Optional.empty() if the accountId is invalid, the amount is not positive,
     * or the account does not exist.
     */
    Optional<Integer> deposit(int timestamp, String accountId, int amount);

    /**
     * Transfers a given amount of money from a source account to a target account.
     * The description in the provided image referred to a transfer operation.
     *
     * @param timestamp The timestamp of the transfer operation.
     * @param sourceAccountId The identifier of the account from which money is transferred.
     * @param targetAccountId The identifier of the account to which money is transferred.
     * @param amount The amount of money to transfer. Must be greater than 0.
     * @return An Optional containing the *new balance of the source account* if the transfer was successful.
     * Returns Optional.empty() otherwise under the following conditions:
     * - If sourceAccountId or targetAccountId is invalid (null or empty).
     * - If sourceAccountId or targetAccountId does not exist.
     * - If sourceAccountId and targetAccountId are the same.
     * - If sourceAccountId has insufficient funds to perform the transfer.
     */
    Optional<Integer> transfer(int timestamp, String sourceAccountId, String targetAccountId, int amount);

    /**
     * Identifies the top 'n' accounts with the highest total outgoing transactions.
     * Total outgoing transactions include money transferred out of an account or withdrawn.
     * (A "pay" operation mentioned in the image is assumed to also contribute to outgoing transactions,
     * but is not explicitly implemented as a separate method here).
     * Cashback operations (from Level 3, not yet implemented) should NOT be reflected in calculations.
     *
     * @param timestamp The timestamp of the request (can be ignored for calculation purposes,
     * but present in method signature).
     * @param n The number of top spending accounts to return.
     * @return A List of strings in the format:
     * `["<accountId1>|<totalOutgoing1>", "<accountId2>|<totalOutgoing2>", ..., "<accountIdN>|<totalOutgoingN>"]`.
     * The list should be sorted:
     * 1. In descending order of total outgoing transactions.
     * 2. In case of a tie in total outgoing transactions, sorted alphabetically by accountId in ascending order.
     * If less than 'n' accounts exist in the system, return all their identifiers in the described format.
     * Returns an empty list if n is less than or equal to 0.
     */
    List<String> topSpenders(int timestamp, int n);

    Optional<Account> accountById(String accountId);
}
