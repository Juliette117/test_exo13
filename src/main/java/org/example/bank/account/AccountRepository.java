package org.example.bank.account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findByNumber(String number);

    boolean existsByNumber(String number);

    List<Account> findAll();
}
