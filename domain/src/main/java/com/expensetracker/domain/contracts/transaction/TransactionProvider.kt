package com.expensetracker.domain.contracts.transaction

import com.expensetracker.core.models.Expense
import com.expensetracker.core.models.Income
import com.expensetracker.core.models.Transaction
import com.expensetracker.core.models.TransactionID
import com.expensetracker.core.models.Transfer
import java.time.LocalDate


interface TransactionProvider {
    fun getTransaction(transactionID: TransactionID): Transaction?
    fun getIncome(transactionID: TransactionID): Income?
    fun getExpense(transactionID: TransactionID): Expense?
    fun getTransfer(transactionID: TransactionID): Transfer?
    fun getIncomeBetween(from: LocalDate, to: LocalDate,predicate: (Transaction) -> Boolean = {true} ): List<Income>
    fun getExpenseBetween(from: LocalDate, to: LocalDate,predicate: (Transaction) -> Boolean = {true} ): List<Expense>
    fun getTransferBetween(from: LocalDate, to: LocalDate,predicate: (Transaction) -> Boolean = {true} ): List<Transfer>
    fun getTransactionsBetween(from: LocalDate, to: LocalDate,predicate: (Transaction) -> Boolean = {true} ): List<Transaction>

    fun getTransactions(offset: Long = 0, limit: Long = 10, predicate: (Transaction) -> Boolean = {true}): List<Transaction>
    fun getIncomes(offset: Long = 0, limit: Long = 10, predicate: (Income) -> Boolean = {true}): List<Transaction>
    fun getExpenses(offset: Long = 0, limit: Long = 10, predicate: (Expense) -> Boolean = {true}): List<Transaction>
    fun getTransfers(offset: Long = 0, limit: Long = 10, predicate: (Transfer) -> Boolean = {true}): List<Transaction>
    fun getTransactionsBetween(from: LocalDate, to: LocalDate, offset: Long = 0, limit: Long = 10, predicate: (Transaction) -> Boolean = {true} ): List<Transaction>
    fun getIncomesBetween(from: LocalDate, to: LocalDate, offset: Long = 0, limit: Long = 10, predicate: (Income) -> Boolean = {true}): List<Transaction>
    fun getExpensesBetween(from: LocalDate, to: LocalDate, offset: Long = 0, limit: Long = 10, predicate: (Expense) -> Boolean = {true} ): List<Transaction>
    fun getTransfersBetween(from: LocalDate, to: LocalDate, offset: Long = 0, limit: Long = 10, predicate: (Transfer) -> Boolean = {true} ): List<Transaction>
}