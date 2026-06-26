package org.example.bank.bdd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.example.bank.account.Account;
import org.example.bank.account.AccountService;
import org.example.bank.account.InMemoryAccountRepository;
import org.example.bank.account.exception.InsufficientFundsException;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AccountStepDefinitions {

    private AccountService accountService;

    @Before
    public void setUp() {
        accountService = new AccountService(new InMemoryAccountRepository());
    }

    @Given("account {string} belongs to {string}")
    public void accountBelongsToHolder(String number, String holder) {
        accountService.createAccount(number, holder);
    }

    @Given("account {string} belongs to {string} with balance {bigdecimal}")
    public void accountBelongsToHolderWithBalance(String number, String holder, BigDecimal balance) {
        accountService.createAccount(number, holder);
        accountService.deposit(number, balance);
    }

    @When("I create account {string} for {string}")
    public void iCreateAccountFor(String number, String holder) {
        accountService.createAccount(number, holder);
    }

    @When("I deposit {bigdecimal} into account {string}")
    public void iDepositIntoAccount(BigDecimal amount, String number) {
        accountService.deposit(number, amount);
    }

    @When("I withdraw {bigdecimal} from account {string}")
    public void iWithdrawFromAccount(BigDecimal amount, String number) {
        accountService.withdraw(number, amount);
    }

    @When("I try to withdraw {bigdecimal} from account {string}")
    public void iTryToWithdrawFromAccount(BigDecimal amount, String number) {
        assertThatThrownBy(() -> accountService.withdraw(number, amount))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @When("I transfer {bigdecimal} from account {string} to account {string}")
    public void iTransferFromAccountToAccount(BigDecimal amount, String sourceNumber, String targetNumber) {
        accountService.transfer(sourceNumber, targetNumber, amount);
    }

    @When("I try to transfer {bigdecimal} from account {string} to account {string}")
    public void iTryToTransferFromAccountToAccount(BigDecimal amount, String sourceNumber, String targetNumber) {
        assertThatThrownBy(() -> accountService.transfer(sourceNumber, targetNumber, amount))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Then("account {string} belongs to {string}")
    public void accountBelongsTo(String number, String holder) {
        Account account = accountService.getAccount(number);
        assertThat(account.getHolder()).isEqualTo(holder);
    }

    @Then("account {string} balance is {bigdecimal}")
    public void accountBalanceIs(String number, BigDecimal expectedBalance) {
        Account account = accountService.getAccount(number);
        assertThat(account.getBalance()).isEqualByComparingTo(expectedBalance);
    }

    @Then("the operation is refused for insufficient funds")
    public void operationIsRefusedForInsufficientFunds() {
        // The assertion is performed in the previous action step.
    }
}
