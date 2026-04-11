package com.bank.cms.controller;

import com.bank.cms.api.response.ApiResponse;
import com.bank.cms.dto.request.TransactionRequest;
import com.bank.cms.entity.Transaction;
import com.bank.cms.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;


    @PostMapping("/fund/transfer")
    private ApiResponse<String> fundTransfer(@RequestBody TransactionRequest request){
        String response = transactionService.transferMoney(request);

        return new ApiResponse<>(
                "Success",
                "Fund Transfer Successful",
                response
        );
    }


    @GetMapping("/{fromAccount}/{toAccount}")
    private ApiResponse<List<Transaction>> getTransaction(@PathVariable String fromAccount,@PathVariable String toAccount){
        return new ApiResponse<>(
                "Success",
                "Account information fetched",
                transactionService.getTransaction(fromAccount,toAccount)
        );
    }

}
