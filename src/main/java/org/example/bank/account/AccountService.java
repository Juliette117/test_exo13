package org.example.bank.account;

import java.math.BigDecimal;
import java.util.List;
import org.example.bank.account.exception.AccountNotFoundException;
import org.example.bank.account.exception.DuplicateAccountException;
import org.example.bank.account.exception.InsufficientFundsException;
import org.example.bank.account.exception.InvalidAmountException;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(String number, String holder) {
        if (accountRepository.existsByNumber(number)) {
            throw new DuplicateAccountException(number);
        }
        Account account = new Account(number, holder, BigDecimal.ZERO);
        accountRepository.save(account);
        return account;
    }

    public Account getAccount(String number) {
        return accountRepository.findByNumber(number)
                .orElseThrow(() -> new AccountNotFoundException(number));
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account deposit(String number, BigDecimal amount) {
        validatePositiveAmount(amount);
        Account account = getAccount(number);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        return account;
    }

    public Account withdraw(String number, BigDecimal amount) {
        validatePositiveAmount(amount);
        Account account = getAccount(number);
        ensureSufficientFunds(account, amount);
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        return account;
    }

    public void transfer(String sourceNumber, String targetNumber, BigDecimal amount) {
        validatePositiveAmount(amount);
        Account source = getAccount(sourceNumber);
        Account target = getAccount(targetNumber);
        ensureSufficientFunds(source, amount);

        source.setBalance(source.getBalance().subtract(amount));
        target.setBalance(target.getBalance().add(amount));
        accountRepository.save(source);
        accountRepository.save(target);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException();
        }
    }

    private void ensureSufficientFunds(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
    }
}
