package com.expensetracker.domain.contracts.transaction

import com.expensetracker.core.models.Account
import com.expensetracker.core.models.Category
import com.expensetracker.core.models.Expense
import com.expensetracker.core.models.Income
import com.expensetracker.core.models.Transaction
import com.expensetracker.core.models.Transfer
import com.expensetracker.domain.support.Result
import java.time.LocalDate

interface TransactionManager: TransactionProvider {
    fun addIncome(_date: LocalDate, _amount: String, _note: String, _description: String = "", category: Category, account: Account): Result
    fun addExpense(_date: LocalDate, _amount: String, _note: String, _description: String = "", category: Category, account: Account): Result
    fun addTransfer(_date: LocalDate, _amount: String, _note: String, _description: String = "", fromAccount: Account, toAccount: Account): Result
    fun updateIncome(transaction: Income, _date: LocalDate? = null, _amount: String? = null, _note: String? = null, _description: String? = null, category: Category? = null, account: Account? = null): Result
    fun updateExpense(transaction: Expense, _date: LocalDate? = null, _amount: String? = null, _note: String? = null, _description: String? = null, category: Category? = null, account: Account? = null): Result
    fun updateTransfer(transaction: Transfer, _date: LocalDate? = null, _amount: String? = null, _note: String? = null, _description: String? = null, fromAccount: Account? = null, toAccount: Account? = null): Result
    fun deleteTransaction(transaction: Transaction): Result
}