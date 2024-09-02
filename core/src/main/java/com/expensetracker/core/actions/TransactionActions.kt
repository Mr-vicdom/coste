package com.expensetracker.core.actions


import com.expensetracker.core.models.Transaction
import com.expensetracker.core.models.TransactionDate
import com.expensetracker.core.models.TransactionID
import com.expensetracker.core.support.Amount
import com.expensetracker.core.support.TransactionResponse

interface TransactionActions<T: Transaction> {
    fun generateId(): Int
    fun hasTransaction(id: TransactionID): Boolean
    fun getTransaction(id: TransactionID): T?
    fun getSomeTransactions(offset: Long, limit: Long = 10, predicate: (T) -> Boolean = {true} ): List<T>
    fun getSomeTransactionsBetween(from: TransactionDate, to: TransactionDate, offset: Long, limit: Long = 10, predicate: (T) -> Boolean = {true} ): List<T>
    fun getTransactions(from: TransactionDate, to: TransactionDate): List<T>
    fun getTotalOfTransactions(from: TransactionDate, to: TransactionDate): Amount
    fun getTransactionsCount(from: TransactionDate, to: TransactionDate): Int
    fun addTransaction(transaction: T): TransactionResponse
    fun updateTransaction(transaction: T): TransactionResponse
    fun deleteTransaction(id: TransactionID): TransactionResponse
}