package com.expensetracker.core.models

import com.expensetracker.core.support.Amount
import com.expensetracker.core.support.SimpleName
import com.expensetracker.core.support.TransactionDescription

sealed class FinancialTransaction(
    override val id: TransactionID,
    override val date: TransactionDate,
    override val amount: Amount,
    override val note: SimpleName,
    override val description: TransactionDescription,
    open val category: Category,
    open val account: Account,
): Transaction(id, date, amount, note, description)

data class Income(
    override val id: TransactionID,
    override val date: TransactionDate,
    override val amount: Amount,
    override val note: SimpleName,
    override val description: TransactionDescription,
    override val category: Category,
    override val account: Account
) : FinancialTransaction(id, date, amount, note, description,category, account)

data class Expense(
    override val id: TransactionID,
    override val date: TransactionDate,
    override val amount: Amount,
    override val note: SimpleName,
    override val description: TransactionDescription,
    override val category: Category,
    override val account: Account
) : FinancialTransaction(id, date, amount, note, description, category, account)