package com.expensetracker.core.models

import com.expensetracker.core.support.Amount
import com.expensetracker.core.support.SimpleName

typealias BankAccountID = AccountID
typealias CashAccountID = AccountID

sealed class PrincipalAccount(
    override val id: AccountID,
    override val name: SimpleName,
    override val balance: Amount,
    open val minimumBalance: Amount
): Account(id, name, balance)

data class BankAccount(
    override val id: BankAccountID,
    override val name: SimpleName,
    override val balance: Amount,
    override val minimumBalance: Amount
) : PrincipalAccount(id, name, balance, minimumBalance)

data class CashAccount(
    override val id: CashAccountID,
    override val name: SimpleName,
    override val balance: Amount,
    override val minimumBalance: Amount
) : PrincipalAccount(id, name, balance, minimumBalance)