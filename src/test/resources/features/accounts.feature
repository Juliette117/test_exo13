# language: en
Feature: Bank account management

  Scenario: Create a new account
    When I create account "ACC-001" for "Alice"
    Then account "ACC-001" belongs to "Alice"
    And account "ACC-001" balance is 0

  Scenario: Deposit money into an account
    Given account "ACC-001" belongs to "Alice"
    When I deposit 50 into account "ACC-001"
    Then account "ACC-001" balance is 50

  Scenario: Withdraw money with sufficient funds
    Given account "ACC-001" belongs to "Alice" with balance 100
    When I withdraw 40 from account "ACC-001"
    Then account "ACC-001" balance is 60

  Scenario: Refuse withdrawal when funds are insufficient
    Given account "ACC-001" belongs to "Alice" with balance 20
    When I try to withdraw 40 from account "ACC-001"
    Then the operation is refused for insufficient funds

  Scenario: Transfer money between accounts
    Given account "ACC-001" belongs to "Alice" with balance 100
    And account "ACC-002" belongs to "Bob" with balance 25
    When I transfer 30 from account "ACC-001" to account "ACC-002"
    Then account "ACC-001" balance is 70
    And account "ACC-002" balance is 55

  Scenario: Refuse transfer when funds are insufficient
    Given account "ACC-001" belongs to "Alice" with balance 10
    And account "ACC-002" belongs to "Bob" with balance 25
    When I try to transfer 30 from account "ACC-001" to account "ACC-002"
    Then the operation is refused for insufficient funds
