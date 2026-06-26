package org.example.bank.account;

import java.math.BigDecimal;

public class Account {

    private final String number;
    private final String holder;
    private BigDecimal balance;

    public Account(String number, String holder, BigDecimal balance) {
        this.number = number;
        this.holder = holder;
        this.balance = balance;
    }

    public String getNumber() {
        return number;
    }

    public String getHolder() {
        return holder;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
