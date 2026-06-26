package org.example.bank.account.exception;

public class DuplicateAccountException extends RuntimeException {

    public DuplicateAccountException(String number) {
        super("Account already exists: " + number);
    }
}
