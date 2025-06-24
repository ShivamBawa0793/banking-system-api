package com.example.bankingsystem.controller;

import com.example.bankingsystem.dto.DepositRequest;
import com.example.bankingsystem.dto.TransferRequest;
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

    @PostMapping("/accounts")
    public ResponseEntity<String> createAccount(@RequestParam String accountId){
        int timeStamp = (int) (System.currentTimeMillis()/1000);
        boolean created = bankingSystem.createAccount(timeStamp,accountId);

        if(created){
            return new ResponseEntity<>("Account:: "+accountId+" created successfully"
                    ,HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Account:: "+accountId+" already exists or is invalid"
                ,HttpStatus.CONFLICT);
    }
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody DepositRequest depositRequest){
        int timeStamp = (int) (System.currentTimeMillis()/1000);
        Optional<Integer> balance = bankingSystem.deposit(timeStamp,
                depositRequest.getAccountId(), depositRequest.getAmount());
        if(balance.isPresent()){
            return new ResponseEntity<>("Deposit successful. " +
                    "New balanace for account id "+depositRequest.getAccountId()+ " is: "
                    +balance, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Deposit failed. " +
                    "account not found or invalid amount", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest transferRequest){
        int timeStamp = (int) (System.currentTimeMillis()/1000);
        Optional<Integer> newBalance = bankingSystem.transfer(timeStamp,
                transferRequest.getSourceAccountId(), transferRequest.getTargetAccountId(),
                transferRequest.getAmount());
        if(newBalance.isPresent()){
            return  new ResponseEntity<>("Transfer success , new balance of the source account: "
                    +transferRequest.getSourceAccountId()+
                    " is :: "+newBalance.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Transfer failed. " +
                    "account not found or invalid amount", HttpStatus.BAD_REQUEST);
        }
    }
}
