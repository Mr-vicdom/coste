package com.expensetracker.data.services.transactions

import com.expensetracker.core.actions.TransactionActions
import com.expensetracker.core.models.Transaction
import com.expensetracker.core.models.TransactionDate
import com.expensetracker.core.models.TransactionID
import com.expensetracker.core.support.Amount
import com.expensetracker.core.support.TransactionResponse
import com.expensetracker.data.services.support.IdGenerator

abstract class TransactionService<T: Transaction>(
    private val transactions: MutableMap<TransactionID, T>,
    private val idGenerator: IdGenerator
): TransactionActions<T>{

    override fun generateId(): Int = idGenerator.newId

    override fun addTransaction(transaction: T): TransactionResponse {
        if(transactions.contains(transaction.id)) return TransactionResponse.TRANSACTION_ALREADY_EXIST
        transactions[transaction.id] = transaction
        return TransactionResponse.TRANSACTION_CREATED
    }

    override fun getTransaction(id: TransactionID): T? = transactions[id]

    override fun getTransactions(from: TransactionDate, to: TransactionDate): List<T> {
        return transactions.filterValues {
            (it.date in from..to)
        }.values.toList()
    }

    override fun getSomeTransactions(
        offset: Long,
        limit: Long,
        predicate: (T) -> Boolean
    ): List<T> {
        return transactions.filterValues {
            predicate(it)
        }.values.toList().dropLast(offset.toInt()).takeLast(limit.toInt()).sortedByDescending { it.id }
    }

    override fun getSomeTransactionsBetween(
        from: TransactionDate,
        to: TransactionDate,
        offset: Long,
        limit: Long,
        predicate: (T) -> Boolean
    ): List<T> {
        return transactions.filterValues {
            (it.date in from..to) && predicate(it)
        }.values.toList().dropLast(offset.toInt()).takeLast(limit.toInt()).sortedByDescending{ it.id }
    }

    override fun getTotalOfTransactions(from: TransactionDate, to: TransactionDate): Amount {
        var total = 0F

        transactions.filterValues {
            it.date in from..to
        }.values.forEach { total += it.amount.value.toFloat() }

        return Amount(total.toString())
    }

    override fun getTransactionsCount(from: TransactionDate, to: TransactionDate): Int {
        return transactions.filterValues {
            it.date in from..to
        }.values.size
    }

    override fun updateTransaction(id: TransactionID, transaction: T): TransactionResponse {
        if(transactions.contains(id)){
            transactions[id] = transaction
            return TransactionResponse.TRANSACTION_UPDATED
        } else return TransactionResponse.TRANSACTION_NOT_EXIST
    }

    override fun deleteTransaction(id: TransactionID): TransactionResponse {
        return transactions.remove(id).let {
            if(it == null)
                TransactionResponse.TRANSACTION_NOT_EXIST
            else
                TransactionResponse.TRANSACTION_DELETED
        }
    }
}