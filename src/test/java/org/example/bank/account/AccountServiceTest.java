package org.example.bank.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.example.bank.account.exception.AccountNotFoundException;
import org.example.bank.account.exception.DuplicateAccountException;
import org.example.bank.account.exception.InsufficientFundsException;
import org.example.bank.account.exception.InvalidAmountException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldCreateAccountWithZeroBalance() {
        // Arrange
        when(accountRepository.existsByNumber("ACC-001")).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Account account = accountService.createAccount("ACC-001", "Alice");

        // Assert
        assertThat(account.getNumber()).isEqualTo("ACC-001");
        assertThat(account.getHolder()).isEqualTo("Alice");
        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldRefuseDuplicateAccountNumber() {
        // Arrange
        when(accountRepository.existsByNumber("ACC-001")).thenReturn(true);

        // Act / Assert
        assertThatThrownBy(() -> accountService.createAccount("ACC-001", "Alice"))
                .isInstanceOf(DuplicateAccountException.class);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldGetExistingAccount() {
        // Arrange
        Account account = new Account("ACC-001", "Alice", BigDecimal.TEN);
        when(accountRepository.findByNumber("ACC-001")).thenReturn(Optional.of(account));

        // Act
        Account result = accountService.getAccount("ACC-001");

        // Assert
        assertThat(result).isSameAs(account);
    }

    @Test
    void shouldThrowWhenAccountIsMissing() {
        // Arrange
        when(accountRepository.findByNumber("ACC-404")).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> accountService.getAccount("ACC-404"))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void shouldGetAllAccounts() {
        // Arrange
        List<Account> accounts = List.of(
                new Account("ACC-001", "Alice", BigDecimal.ZERO),
                new Account("ACC-002", "Bob", BigDecimal.TEN)
        );
        when(accountRepository.findAll()).thenReturn(accounts);

        // Act
        List<Account> result = accountService.getAllAccounts();

        // Assert
        assertThat(result).containsExactlyElementsOf(accounts);
    }

    @Test
    void shouldDepositPositiveAmount() {
        // Arrange
        Account account = new Account("ACC-001", "Alice", BigDecimal.TEN);
        when(accountRepository.findByNumber("ACC-001")).thenReturn(Optional.of(account));

        // Act
        Account result = accountService.deposit("ACC-001", new BigDecimal("25.50"));

        // Assert
        assertThat(result.getBalance()).isEqualByComparingTo("35.50");
        verify(accountRepository).save(account);
    }

    @Test
    void shouldRefuseZeroDeposit() {
        // Arrange / Act / Assert
        assertThatThrownBy(() -> accountService.deposit("ACC-001", BigDecimal.ZERO))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void shouldRefuseNegativeDeposit() {
        // Arrange / Act / Assert
        assertThatThrownBy(() -> accountService.deposit("ACC-001", new BigDecimal("-1")))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void shouldWithdrawPositiveAmountWhenFundsAreSufficient() {
        // Arrange
        Account account = new Account("ACC-001", "Alice", new BigDecimal("100"));
        when(accountRepository.findByNumber("ACC-001")).thenReturn(Optional.of(account));

        // Act
        Account result = accountService.withdraw("ACC-001", new BigDecimal("40"));

        // Assert
        assertThat(result.getBalance()).isEqualByComparingTo("60");
        verify(accountRepository).save(account);
    }

    @Test
    void shouldRefuseZeroWithdraw() {
        // Arrange / Act / Assert
        assertThatThrownBy(() -> accountService.withdraw("ACC-001", BigDecimal.ZERO))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void shouldRefuseNegativeWithdraw() {
        // Arrange / Act / Assert
        assertThatThrownBy(() -> accountService.withdraw("ACC-001", new BigDecimal("-1")))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void shouldRefuseWithdrawWhenFundsAreInsufficient() {
        // Arrange
        Account account = new Account("ACC-001", "Alice", new BigDecimal("20"));
        when(accountRepository.findByNumber("ACC-001")).thenReturn(Optional.of(account));

        // Act / Assert
        assertThatThrownBy(() -> accountService.withdraw("ACC-001", new BigDecimal("30")))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void shouldTransferAmountBetweenAccounts() {
        // Arrange
        Account source = new Account("ACC-001", "Alice", new BigDecimal("100"));
        Account target = new Account("ACC-002", "Bob", new BigDecimal("20"));
        when(accountRepository.findByNumber("ACC-001")).thenReturn(Optional.of(source));
        when(accountRepository.findByNumber("ACC-002")).thenReturn(Optional.of(target));

        // Act
        accountService.transfer("ACC-001", "ACC-002", new BigDecimal("30"));

        // Assert
        assertThat(source.getBalance()).isEqualByComparingTo("70");
        assertThat(target.getBalance()).isEqualByComparingTo("50");
        verify(accountRepository).save(source);
        verify(accountRepository).save(target);
    }

    @Test
    void shouldRefuseZeroTransfer() {
        // Arrange / Act / Assert
        assertThatThrownBy(() -> accountService.transfer("ACC-001", "ACC-002", BigDecimal.ZERO))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void shouldRefuseNegativeTransfer() {
        // Arrange / Act / Assert
        assertThatThrownBy(() -> accountService.transfer("ACC-001", "ACC-002", new BigDecimal("-1")))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void shouldRefuseTransferWhenFundsAreInsufficient() {
        // Arrange
        Account source = new Account("ACC-001", "Alice", new BigDecimal("20"));
        Account target = new Account("ACC-002", "Bob", BigDecimal.ZERO);
        when(accountRepository.findByNumber("ACC-001")).thenReturn(Optional.of(source));
        when(accountRepository.findByNumber("ACC-002")).thenReturn(Optional.of(target));

        // Act / Assert
        assertThatThrownBy(() -> accountService.transfer("ACC-001", "ACC-002", new BigDecimal("30")))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void shouldRefuseTransferToMissingAccount() {
        // Arrange
        Account source = new Account("ACC-001", "Alice", new BigDecimal("100"));
        when(accountRepository.findByNumber("ACC-001")).thenReturn(Optional.of(source));
        when(accountRepository.findByNumber("ACC-404")).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> accountService.transfer("ACC-001", "ACC-404", new BigDecimal("30")))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void shouldRefuseTransferFromMissingAccount() {
        // Arrange
        when(accountRepository.findByNumber("ACC-404")).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> accountService.transfer("ACC-404", "ACC-002", new BigDecimal("30")))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
