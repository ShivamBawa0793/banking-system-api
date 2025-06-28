package com.example.bankingsystem.controller;

import com.example.bankingsystem.dto.request.DepositRequest;
import com.example.bankingsystem.dto.request.TransferRequest;
import com.example.bankingsystem.dto.response.AccountDetailsResponse;
import com.example.bankingsystem.dto.response.ApiResponse;
import com.example.bankingsystem.model.Account;
import com.example.bankingsystem.service.BankingSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bank")
public class BankingController {

    // Initialize a logger for this class
    private static final Logger logger = LoggerFactory.getLogger(BankingController.class);

    private BankingSystem bankingSystem;

    // Constructor injection for BankingSystem service
    @Autowired
    public BankingController(BankingSystem bankingSystem) {
        this.bankingSystem = bankingSystem;
        logger.info("BankingController initialized with BankingSystem service.");
    }

    /**
     * Retrieves account details by account ID.
     * @param accountId The ID of the account to retrieve.
     * @return ResponseEntity containing AccountDetailsResponse if found, or ApiResponse with error if not found.
     */
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<?> getAccountById(@PathVariable String accountId) {
        logger.info("Received GET request for /api/bank/accounts/{} to get account details.", accountId);

        Optional<Account> accountOpt = bankingSystem.accountById(accountId);
        AccountDetailsResponse responseDto = null;
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            responseDto = new AccountDetailsResponse(
                    account.getAccountId(),
                    account.getTimestamp(),
                    account.getBalance(),
                    account.getTotalOutgoing()
            );
            logger.info("Account with ID {} found. Returning account details.", accountId);
            logger.debug("Account details response: {}", responseDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } else {
            logger.warn("Account with ID {} not found.", accountId);
            return new ResponseEntity<>(new ApiResponse("ACCOUNT_NOT_FOUND", "Account not found", false), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves details for all accounts.
     * @return ResponseEntity containing a list of AccountDetailsResponse.
     */
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDetailsResponse>> getAllAccount() {
        logger.info("Received GET request for /api/bank/accounts to get all accounts.");

        List<Account> allAccounts = bankingSystem.getAllAccounts();
        List<AccountDetailsResponse> responseList = allAccounts.stream()
                .map(account -> new AccountDetailsResponse(
                        account.getAccountId(),
                        account.getTimestamp(),
                        account.getBalance(),
                        account.getTotalOutgoing()))
                .toList();

        logger.info("Returning details for {} accounts.", responseList.size());
        logger.debug("All accounts response: {}", responseList);
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    /**
     * Checks the balance for a specific account.
     * @param accountId The ID of the account whose balance is to be checked.
     * @return ResponseEntity containing ApiResponse with balance details or an error message.
     */
    @GetMapping("accounts/balance/{accountId}")
    ResponseEntity<ApiResponse> checkBalance(@PathVariable String accountId) {
        logger.info("Received GET request for /api/bank/accounts/balance/{} to check balance.", accountId);

        Optional<Integer> balance = bankingSystem.getBalance(accountId);
        if (balance.isPresent()) {
            logger.info("Balance for account ID {} is {}.", accountId, balance.get());
            return new ResponseEntity<>(new ApiResponse("Account ID " + accountId + " balance is : " + balance.get(),
                    true),
                    HttpStatus.OK);
        } else {
            logger.warn("Attempted to check balance for non-existent account ID {}.", accountId);
            return new ResponseEntity<>(new ApiResponse("ACCOUNT_DOESNOT_EXISIT",
                    "Account " + accountId + " invalid", false),
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * Creates a new account.
     * @param accountId The ID for the new account.
     * @return ResponseEntity indicating success or failure of account creation.
     */
    @PostMapping("/accounts")
    public ResponseEntity<ApiResponse> createAccount(@RequestParam String accountId) {
        logger.info("Received POST request for /api/bank/accounts to create account with ID: {}.", accountId);
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        boolean created = bankingSystem.createAccount(timeStamp, accountId);

        if (created) {
            logger.info("Account {} created successfully.", accountId);
            return new ResponseEntity<>(new ApiResponse("Account " + accountId + " created successfully.", true),
                    HttpStatus.CREATED);
        }
        logger.warn("Failed to create account {}. Account already exists or invalid ID.", accountId);
        return new ResponseEntity<>(new ApiResponse("ACCOUNT_EXISTS",
                "Account " + accountId + " already exists or invalid ID.", false),
                HttpStatus.CONFLICT);
    }

    /**
     * Handles deposit operations for an account.
     * @param depositRequest The request body containing account ID and amount.
     * @return ResponseEntity indicating success or failure of deposit.
     */
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse> deposit(@RequestBody DepositRequest depositRequest) {
        logger.info("Received POST request for /api/bank/deposit. Account ID: {}, Amount: {}.",
                depositRequest.getAccountId(), depositRequest.getAmount());

        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        Optional<Integer> balance = bankingSystem.deposit(timeStamp,
                depositRequest.getAccountId(), depositRequest.getAmount());
        if (balance.isPresent()) {
            logger.info("Deposit successful for account ID {}. New balance: {}.",
                    depositRequest.getAccountId(), balance.get());
            return new ResponseEntity<ApiResponse>(new ApiResponse("Deposit successful." +
                    "new balance for account id " + depositRequest.getAccountId() + " is: "
                    + balance.get(), true), HttpStatus.OK);
        } else {
            logger.warn("Deposit failed for account ID {}. Invalid account or amount: {}.",
                    depositRequest.getAccountId(), depositRequest.getAmount());
            return new ResponseEntity<ApiResponse>(new ApiResponse("DEPOSIT_FAILED", "Deposit failed. " +
                    "account not found or invalid amount", false), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Handles fund transfer between accounts.
     * @param transferRequest The request body containing source, target account IDs and amount.
     * @return ResponseEntity indicating success or failure of transfer.
     */
    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse> transfer(@RequestBody TransferRequest transferRequest) {
        logger.info("Received POST request for /api/bank/transfer. Source: {}, Target: {}, Amount: {}.",
                transferRequest.getSourceAccountId(), transferRequest.getTargetAccountId(), transferRequest.getAmount());

        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        Optional<Integer> newBalance = bankingSystem.transfer(timeStamp,
                transferRequest.getSourceAccountId(), transferRequest.getTargetAccountId(),
                transferRequest.getAmount());
        if (newBalance.isPresent()) {
            logger.info("Transfer successful from {} to {}. New balance for source: {}.",
                    transferRequest.getSourceAccountId(), transferRequest.getTargetAccountId(), newBalance.get());
            return new ResponseEntity<ApiResponse>(new ApiResponse("Transfer success , new balance of the source account: "
                    + transferRequest.getSourceAccountId() +
                    " is :: " + newBalance.get(), true), HttpStatus.OK);
        } else {
            logger.warn("Transfer failed from {} to {}. Invalid accounts or amount: {}.",
                    transferRequest.getSourceAccountId(), transferRequest.getTargetAccountId(), transferRequest.getAmount());
            return new ResponseEntity<ApiResponse>(new ApiResponse("INVALID_AMOUNT", "Transfer failed. " +
                    "account not found or invalid amount", false), HttpStatus.BAD_REQUEST);
        }
    }
}
