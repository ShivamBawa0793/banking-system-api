package com.example.bankingsystem.repository;


import com.example.bankingsystem.model.Account;

public interface AccountRepository {
    /**
     * Saves a new account to the database.
     * @param accountId The Account_ID to save.
     * @return true if the account id was saved, false if an account with the same ID already exists.
     */
    Boolean save(int timeStamp, String accountId);

}
