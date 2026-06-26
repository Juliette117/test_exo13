package org.example.bank.account.exception;

public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException() {
        super("Amount must be strictly positive");
    }
}
