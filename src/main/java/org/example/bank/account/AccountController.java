package org.example.bank.account;

import java.util.List;
import org.example.bank.account.dto.CreateAccountRequest;
import org.example.bank.account.dto.MoneyRequest;
import org.example.bank.account.dto.TransferRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(@RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request.number(), request.holder());
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{number}")
    public Account getAccount(@PathVariable String number) {
        return accountService.getAccount(number);
    }

    @PostMapping("/{number}/deposit")
    public Account deposit(@PathVariable String number, @RequestBody MoneyRequest request) {
        return accountService.deposit(number, request.amount());
    }

    @PostMapping("/{number}/withdraw")
    public Account withdraw(@PathVariable String number, @RequestBody MoneyRequest request) {
        return accountService.withdraw(number, request.amount());
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@RequestBody TransferRequest request) {
        accountService.transfer(request.sourceNumber(), request.targetNumber(), request.amount());
    }
}
