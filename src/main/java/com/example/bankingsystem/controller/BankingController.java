package com.example.bankingsystem.controller;

import com.example.bankingsystem.service.BankingSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
