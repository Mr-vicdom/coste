package com.expensetracker.domain.concretes.transaction

import com.expensetracker.core.actions.ExpenseActions
import com.expensetracker.core.actions.IncomeActions
import com.expensetracker.core.actions.TransferActions
import com.expensetracker.core.models.Expense
import com.expensetracker.core.models.Income
import com.expensetracker.core.models.Transaction
import com.expensetracker.core.models.TransactionID
import com.expensetracker.core.models.Transfer
import com.expensetracker.core.support.Amount
import com.expensetracker.core.support.Helper
import com.expensetracker.domain.contracts.transaction.TransactionProvider
import java.time.LocalDate

class TransactionProviderImp(
    private val incomeActions: IncomeActions,
    private val expenseActions: ExpenseActions,
    private val transferActions: TransferActions,
) : TransactionProvider {
    override fun getTransaction(transactionID: TransactionID): Transaction? {
        return incomeActions.getTransaction(transactionID)
            ?: expenseActions.getTransaction(transactionID)
            ?: transferActions.getTransaction(transactionID)
    }

    override fun getIncome(transactionID: TransactionID): Income? =
        incomeActions.getTransaction(transactionID)

    override fun getExpense(transactionID: TransactionID): Expense? =
        expenseActions.getTransaction(transactionID)

    override fun getTransfer(transactionID: TransactionID): Transfer? =
        transferActions.getTransaction(transactionID)

    override fun getIncomeBetween(
        from: LocalDate,
        to: LocalDate,
        predicate: (Transaction) -> Boolean,
    ): List<Income> {
        return incomeActions.getTransactions(Helper.dateToMillis(from), Helper.dateToMillis(to))
            .filter(predicate)
    }

    override fun getExpenseBetween(
        from: LocalDate,
        to: LocalDate,
        predicate: (Transaction) -> Boolean,
    ): List<Expense> {
        return expenseActions.getTransactions(Helper.dateToMillis(from), Helper.dateToMillis(to))
            .filter(predicate)
    }

    override fun getTransferBetween(
        from: LocalDate,
        to: LocalDate,
        predicate: (Transaction) -> Boolean,
    ): List<Transfer> {
        return transferActions.getTransactions(Helper.dateToMillis(from), Helper.dateToMillis(to))
            .filter(predicate)
    }

    override fun getTransactions(
        offset: Long,
        limit: Long,
        predicate: (Transaction) -> Boolean,
    ): List<Transaction> {
        val transactions: MutableList<Transaction> = mutableListOf()
        transactions.addAll(getIncomes(offset, limit, predicate))
        transactions.addAll(getExpenses(offset, limit, predicate))
        transactions.addAll(getTransfers(offset, limit, predicate))
        transactions.sortByDescending { it.date }

        return transactions.filter(predicate)
    }

    override fun getIncomes(
        offset: Long,
        limit: Long,
        predicate: (Income) -> Boolean,
    ): List<Transaction> {

        return incomeActions.getSomeTransactions(
            offset, limit, predicate
        )
    }

    override fun getExpenses(
        offset: Long,
        limit: Long,
        predicate: (Expense) -> Boolean,
    ): List<Transaction> {

        return expenseActions.getSomeTransactions(
            offset, limit, predicate
        )
    }

    override fun getTransfers(
        offset: Long,
        limit: Long,
        predicate: (Transfer) -> Boolean,
    ): List<Transaction> {

        return transferActions.getSomeTransactions(
            offset, limit, predicate
        )
    }

    override fun getTransactionsBetween(
        from: LocalDate,
        to: LocalDate,
        predicate: (Transaction) -> Boolean,
    ): List<Transaction> {
        val transactions: MutableList<Transaction> = mutableListOf()
        with(transactions) {
            addAll(getIncomeBetween(from, to, predicate))
            addAll(getExpenseBetween(from, to, predicate))
            addAll(getTransferBetween(from, to, predicate))
        }
        return transactions
    }


    override fun getTransactionsBetween(
        from: LocalDate,
        to: LocalDate,
        offset: Long,
        limit: Long,
        predicate: (Transaction) -> Boolean,
    ): List<Transaction> {
        val transactions: MutableList<Transaction> = mutableListOf()
        transactions.addAll(getIncomesBetween(from, to, offset, limit, predicate))
        transactions.addAll(getExpensesBetween(from, to, offset, limit, predicate))
        transactions.addAll(getTransfersBetween(from, to, offset, limit, predicate))
        transactions.sortBy { it.id }
        return transactions.take(limit.toInt())
    }

    override fun getIncomesBetween(
        from: LocalDate,
        to: LocalDate,
        offset: Long,
        limit: Long,
        predicate: (Income) -> Boolean,
    ): List<Transaction> {
        val fromMillis = Helper.dateToMillis(from)
        val toMillis = Helper.dateToMillis(to)

        return incomeActions.getSomeTransactionsBetween(
            from = fromMillis,
            to = toMillis,
            offset, limit, predicate
        )
    }

    override fun getExpensesBetween(
        from: LocalDate,
        to: LocalDate,
        offset: Long,
        limit: Long,
        predicate: (Expense) -> Boolean,
    ): List<Transaction> {
        val fromMillis = Helper.dateToMillis(from)
        val toMillis = Helper.dateToMillis(to)

        return expenseActions.getSomeTransactionsBetween(
            from = fromMillis,
            to = toMillis,
            offset, limit, predicate
        )
    }

    override fun getTransfersBetween(
        from: LocalDate,
        to: LocalDate,
        offset: Long,
        limit: Long,
        predicate: (Transfer) -> Boolean,
    ): List<Transaction> {
        val fromMillis = Helper.dateToMillis(from)
        val toMillis = Helper.dateToMillis(to)

        return transferActions.getSomeTransactionsBetween(
            from = fromMillis,
            to = toMillis,
            offset, limit, predicate
        )
    }

    override fun getTotalOfIncomeBetween(from: LocalDate, to: LocalDate): String =
        incomeActions.getTotalOfTransactions(Helper.dateToMillis(from), Helper.dateToMillis(to))
            .toString()

    override fun getTotalOfExpenseBetween(from: LocalDate, to: LocalDate): String =
        expenseActions.getTotalOfTransactions(Helper.dateToMillis(from), Helper.dateToMillis(to))
            .toString()

}