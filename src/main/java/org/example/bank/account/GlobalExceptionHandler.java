package org.example.bank.account;

import java.util.Map;
import org.example.bank.account.exception.AccountNotFoundException;
import org.example.bank.account.exception.DuplicateAccountException;
import org.example.bank.account.exception.InsufficientFundsException;
import org.example.bank.account.exception.InvalidAmountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAccountNotFound(AccountNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(DuplicateAccountException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateAccount(DuplicateAccountException exception) {
        return error(HttpStatus.CONFLICT, exception);
    }

    @ExceptionHandler({InvalidAmountException.class, InsufficientFundsException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException exception) {
        return error(HttpStatus.BAD_REQUEST, exception);
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, RuntimeException exception) {
        return ResponseEntity.status(status).body(Map.of("error", exception.getMessage()));
    }
}
