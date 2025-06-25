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

import java.util.Optional;

@RestController
@RequestMapping("/api/bank")
public class BankingController {
    private BankingSystem bankingSystem;

    //constructor injection
    @Autowired
    public BankingController(BankingSystem bankingSystem){
        this.bankingSystem = bankingSystem;
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<?> getAccountById(@PathVariable String accountId) {

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
            return new ResponseEntity<>(responseDto,HttpStatus.OK);
        }else {
            return new ResponseEntity<>(new ApiResponse("ACCOUNT_NOT_FOUND","Account not found",false),HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/accounts")
    public ResponseEntity<ApiResponse> createAccount(@RequestParam String accountId){
        int timeStamp = (int) (System.currentTimeMillis()/1000);
        boolean created = bankingSystem.createAccount(timeStamp,accountId);

        if(created){
            return new ResponseEntity<>(new ApiResponse("Account "+accountId+ " created successfully." , true),
                    HttpStatus.CREATED);
        }
            return new ResponseEntity<>(new ApiResponse("ACCOUNT_EXISTS" ,
                "Account " +accountId+ " already exists or invalid ID.",false),
                HttpStatus.CONFLICT);
    }
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse> deposit(@RequestBody DepositRequest depositRequest){
        int timeStamp = (int) (System.currentTimeMillis()/1000);
        Optional<Integer> balance = bankingSystem.deposit(timeStamp,
                depositRequest.getAccountId(), depositRequest.getAmount());
        if(balance.isPresent()){
            return new ResponseEntity<ApiResponse>(new ApiResponse("Deposit successful." +
                    "new balance for accont id " +depositRequest.getAccountId()+ " is: "
                    +balance.get(),true), HttpStatus.OK);
        }else{
            return new ResponseEntity<ApiResponse>(new ApiResponse("DEPOSIT_FAILED","Deposit failed. "+
                    "account not found or invalid amount" ,false), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse> transfer(@RequestBody TransferRequest transferRequest){
        int timeStamp = (int) (System.currentTimeMillis()/1000);
        Optional<Integer> newBalance = bankingSystem.transfer(timeStamp,
                transferRequest.getSourceAccountId(), transferRequest.getTargetAccountId(),
                transferRequest.getAmount());
        if(newBalance.isPresent()){
            return  new ResponseEntity<ApiResponse>(new ApiResponse("Transfer success , new balance of the source account: "
                    +transferRequest.getSourceAccountId()+
                    " is :: "+newBalance.get(),true), HttpStatus.OK);
        }else{
            return new ResponseEntity<ApiResponse>(new ApiResponse("INVALID_AMOUNT","Transfer failed. " +
                    "account not found or invalid amount",false), HttpStatus.BAD_REQUEST);
        }
    }
}
