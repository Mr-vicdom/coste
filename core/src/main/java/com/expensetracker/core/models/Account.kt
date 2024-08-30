package com.expensetracker.core.models

import com.expensetracker.core.support.Amount
import com.expensetracker.core.support.Literals.DEFAULT_AMOUNT
import com.expensetracker.core.support.SimpleName

typealias AccountID = Int
typealias CreditCardID = AccountID
typealias DebitCardID = AccountID


sealed class Account(
    open val id: AccountID,
    open val name: SimpleName,
    open val balance: Amount,
)

data class CreditCard(
    override val id: AccountID,
    override val name: SimpleName,
    override val balance: Amount = Amount(
        DEFAULT_AMOUNT
    ),
    val outStandings: Amount = Amount(
        DEFAULT_AMOUNT
    )
) : Account(id, name, balance)

data class DebitCard(
    override val id: AccountID,
    val bankAccount: BankAccount,
    override val name: SimpleName,
    override val balance: Amount = bankAccount.balance,
    val limit: Amount = Amount(DEFAULT_AMOUNT)
) : Account(id, name, balance)