package com.expensetracker.core.models

import com.expensetracker.core.support.Amount
import com.expensetracker.core.support.SimpleName
import com.expensetracker.core.support.TransactionDescription


typealias TransactionID = Int
typealias TransactionDate = Long

sealed class Transaction(
    open val id: TransactionID,
    open val date: TransactionDate,
    open val amount: Amount,
    open val note: SimpleName,
    open val description: TransactionDescription
)

data class Transfer(
    override val id: TransactionID,
    override val date: TransactionDate,
    override val amount: Amount,
    override val note: SimpleName,
    override val description: TransactionDescription,
    val fromAccount: Account,
    val toAccount: Account
): Transaction(id, date, amount, note, description)