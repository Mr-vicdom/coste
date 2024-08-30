package com.expensetracker.domain.concretes.transaction

import com.expensetracker.core.actions.ExpenseActions
import com.expensetracker.core.actions.IncomeActions
import com.expensetracker.core.actions.TransferActions
import com.expensetracker.core.models.Account
import com.expensetracker.core.models.Category
import com.expensetracker.core.models.Expense
import com.expensetracker.core.models.Income
import com.expensetracker.core.models.Transaction
import com.expensetracker.core.models.Transfer
import com.expensetracker.core.support.Amount
import com.expensetracker.core.support.CustomException
import com.expensetracker.core.support.Helper
import com.expensetracker.core.support.SimpleName
import com.expensetracker.core.support.TransactionDescription
import com.expensetracker.core.support.TransactionResponse
import com.expensetracker.domain.concretes.account.AccountManagerImp
import com.expensetracker.domain.contracts.transaction.TransactionManager
import com.expensetracker.domain.contracts.transaction.TransactionProvider
import com.expensetracker.domain.support.Result
import java.time.LocalDate

class TransactionManagerImp(
    private val transactionProvider: TransactionProvider,
    private val incomeActions: IncomeActions,
    private val expenseActions: ExpenseActions,
    private val transferActions: TransferActions,
    private val accountManager: AccountManagerImp
): TransactionManager,
    TransactionProvider by transactionProvider {



    private fun createTransaction(transaction: Transaction) : Result {
        val result = when(transaction){
            is Expense -> expenseActions.addTransaction(transaction)
            is Income -> incomeActions.addTransaction(transaction)
            is Transfer -> transferActions.addTransaction(transaction)
        }

        return if(result == TransactionResponse.TRANSACTION_CREATED){
            return Result.Success(result.toString())
        } else Result.Failure(result.toString())
    }

    override fun addIncome(
        _date: LocalDate,
        _amount: String,
        _note: String,
        _description: String,
        category: Category,
        account: Account,
    ): Result {
        try {
            val id : Int = incomeActions.generateId()
            val date : Long = Helper.dateToMillis(_date)
            val amount : Amount = Amount(_amount)
            val note : SimpleName = SimpleName(_note)
            val description : TransactionDescription = TransactionDescription(_description)
            val result: Result = createTransaction(Income(
                id, date, amount, note, description, category, account
            ))
            if(result is Result.Success){
                val accountResult = accountManager.creditAccount(account, amount)
                if (accountResult is Result.Failure) return accountResult
            }
            return result
        } catch (e: CustomException) {
            return Result.Failure(e.message)
        }
    }

    override fun addExpense(
        _date: LocalDate,
        _amount: String,
        _note: String,
        _description: String,
        category: Category,
        account: Account,
    ): Result {
        try {
            val id : Int = incomeActions.generateId()
            val date : Long = Helper.dateToMillis(_date)
            val amount : Amount = Amount(_amount)
            val note : SimpleName = SimpleName(_note)
            val description : TransactionDescription = TransactionDescription(_description)
            val result: Result = createTransaction(Expense(
                id, date, amount, note, description, category, account
            ))
            if(result is Result.Success){
                val accountResult = accountManager.debitAccount(account, amount)
                if (accountResult is Result.Failure) return accountResult
            }
            return result
        } catch (e: CustomException) {
            return Result.Failure(e.message)
        }
    }

    override fun addTransfer(
        _date: LocalDate,
        _amount: String,
        _note: String,
        _description: String,
        fromAccount: Account,
        toAccount: Account,
    ): Result {
        try {
            val id : Int = incomeActions.generateId()
            val date : Long = Helper.dateToMillis(_date)
            val amount : Amount = Amount(_amount)
            val note : SimpleName = SimpleName(_note)
            val description : TransactionDescription = TransactionDescription(_description)
            val result: Result = createTransaction(Transfer(
                id, date, amount, note, description, fromAccount, toAccount
            ))
            if(result is Result.Success){
                val accountResult1 = accountManager.debitAccount(fromAccount, amount)
                val accountResult2 = accountManager.creditAccount(toAccount, amount)
                if (accountResult1 is Result.Failure) return accountResult1
                if (accountResult2 is Result.Failure) return accountResult2
            }
            return result
        } catch (e: CustomException) {
            return Result.Failure(e.message)
        }
    }

    override fun updateIncome(
        transaction: Income,
        _date: LocalDate?,
        _amount: String?,
        _note: String?,
        _description: String?,
        category: Category?,
        account: Account?
    ) : Result {
        var updatedTransaction = transaction.copy()
        try {
            _date?.let { updatedTransaction = updatedTransaction.copy(date = Helper.dateToMillis(it)) }
            _amount?.let { updatedTransaction = updatedTransaction.copy(amount = Amount(it)) }
            _note?.let { updatedTransaction = updatedTransaction.copy(note = SimpleName(it)) }
            _description?.let { updatedTransaction = updatedTransaction.copy(description = TransactionDescription(it)) }
            category?.let { updatedTransaction = updatedTransaction.copy(category = it) }
            account?.let { updatedTransaction = updatedTransaction.copy(account = it) }
            val transactionResponse = incomeActions.updateTransaction(transaction.id,updatedTransaction)
            if(transactionResponse == TransactionResponse.TRANSACTION_UPDATED){
                val accountResult1 = accountManager.debitAccount(transaction.account, transaction.amount)
                val accountResult2 = accountManager.creditAccount(updatedTransaction.account, updatedTransaction.amount)
                if (accountResult1 is Result.Failure) return accountResult1
                if (accountResult2 is Result.Failure) return accountResult2
                return Result.Success(transactionResponse.toString())
            }
            return Result.Failure(transactionResponse.toString())
        } catch (e : CustomException) {
            return Result.Failure(e.message)
        }
    }

    override fun updateExpense(
        transaction: Expense,
        _date: LocalDate?,
        _amount: String?,
        _note: String?,
        _description: String?,
        category: Category?,
        account: Account?
    ) : Result {
        var updatedTransaction = transaction.copy()
        try {
            _date?.let { updatedTransaction = updatedTransaction.copy(date = Helper.dateToMillis(it)) }
            _amount?.let { updatedTransaction = updatedTransaction.copy(amount = Amount(it)) }
            _note?.let { updatedTransaction = updatedTransaction.copy(note = SimpleName(it)) }
            _description?.let { updatedTransaction = updatedTransaction.copy(description = TransactionDescription(it)) }
            category?.let { updatedTransaction = updatedTransaction.copy(category = it) }
            account?.let { updatedTransaction = updatedTransaction.copy(account = it) }
            val transactionResponse = expenseActions.updateTransaction(transaction.id,updatedTransaction)
            if(transactionResponse == TransactionResponse.TRANSACTION_UPDATED){
                val accountResult1 = accountManager.creditAccount(transaction.account, transaction.amount)
                val accountResult2 = accountManager.debitAccount(updatedTransaction.account, updatedTransaction.amount)
                if (accountResult1 is Result.Failure) return accountResult1
                if (accountResult2 is Result.Failure) return accountResult2
                return Result.Success(transactionResponse.toString())
            }
            return Result.Failure(transactionResponse.toString())
        } catch (e : CustomException) {
            return Result.Failure(e.message)
        }
    }

    override fun updateTransfer(
        transaction: Transfer,
        _date: LocalDate?,
        _amount: String?,
        _note: String?,
        _description: String?,
        fromAccount: Account?,
        toAccount: Account?
    ) : Result {
        var updatedTransaction = transaction.copy()
        try {
            _date?.let { updatedTransaction = updatedTransaction.copy(date = Helper.dateToMillis(it)) }
            _amount?.let { updatedTransaction = updatedTransaction.copy(amount = Amount(it)) }
            _note?.let { updatedTransaction = updatedTransaction.copy(note = SimpleName(it)) }
            _description?.let { updatedTransaction = updatedTransaction.copy(description = TransactionDescription(it)) }
            fromAccount?.let { updatedTransaction = updatedTransaction.copy(fromAccount = it) }
            toAccount?.let { updatedTransaction = updatedTransaction.copy(toAccount = it) }
            val transactionResponse = transferActions.updateTransaction(transaction.id,updatedTransaction)
            if(transactionResponse == TransactionResponse.TRANSACTION_UPDATED){
                val accountResult1 = accountManager.creditAccount(transaction.fromAccount, transaction.amount)
                val accountResult2 = accountManager.debitAccount(updatedTransaction.toAccount, updatedTransaction.amount)
                if (accountResult1 is Result.Failure) return accountResult1
                if (accountResult2 is Result.Failure) return accountResult2

                val accountResult3 = accountManager.debitAccount(transaction.fromAccount, transaction.amount)
                val accountResult4 = accountManager.creditAccount(updatedTransaction.toAccount, updatedTransaction.amount)
                if (accountResult3 is Result.Failure) return accountResult3
                if (accountResult4 is Result.Failure) return accountResult4
                return Result.Success(transactionResponse.toString())
            }
            return Result.Failure(transactionResponse.toString())
        } catch (e : CustomException) {
            return Result.Failure(e.message)
        }
    }

    override fun deleteTransaction(transaction: Transaction): Result {
        val transactionResponse = when(transaction){
            is Expense -> expenseActions.deleteTransaction(transaction.id)
            is Income -> incomeActions.deleteTransaction(transaction.id)
            is Transfer -> transferActions.deleteTransaction(transaction.id)
        }

        return if(transactionResponse == TransactionResponse.TRANSACTION_DELETED) {
            Result.Success(transactionResponse.toString())
        } else Result.Failure(transactionResponse.toString())
    }
}