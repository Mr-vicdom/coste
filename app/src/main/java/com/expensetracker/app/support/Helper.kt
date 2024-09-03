package com.expensetracker.app.support

import android.util.Log
import com.expensetracker.app.transactions.viewmodel.TAG
import com.expensetracker.core.actions.AccountActions
import com.expensetracker.core.actions.CategoryActions
import com.expensetracker.core.actions.TransactionActions
import com.expensetracker.core.models.Account
import com.expensetracker.core.models.Category
import com.expensetracker.core.models.Transaction
import com.expensetracker.core.support.AccountResponse
import com.expensetracker.core.support.CategoryResponse
import com.expensetracker.core.support.TransactionResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Helper {
    fun dateToString(day: Int, month: Int, year: Int): String {
        return "$day/$month/$year"
    }
    fun dateToString(date: LocalDate): String {
        return "${date.dayOfMonth}/${date.monthValue}/${date.year}"
    }
    fun stringToDate(string: String): LocalDate {
        return LocalDate.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }
}

private fun <T : Transaction> checkTransactionService(
    transaction: T,
    service: TransactionActions<T>,
) {
    Log.d(TAG, "onHasNoTransaction: ${!service.hasTransaction(transaction.id)}")
    Log.d(TAG, "onGetNoTransaction: ${service.getTransaction(transaction.id) == null}")
    Log.d(
        TAG,
        "onUpdateNoTransaction: ${service.updateTransaction(transaction) == TransactionResponse.TRANSACTION_NOT_EXIST}"
    )
    Log.d(
        TAG,
        "onDeleteNoTransaction: ${service.deleteTransaction(transaction.id) == TransactionResponse.TRANSACTION_NOT_EXIST}"
    )

    Log.d(
        TAG,
        "onCreateTransaction: ${service.addTransaction(transaction) == TransactionResponse.TRANSACTION_CREATED}"
    )
    Log.d(TAG, "onHasTransaction: ${service.hasTransaction(transaction.id)}")
    Log.d(TAG, "onGetTransaction: ${service.getTransaction(transaction.id) != null}")
    Log.d(
        TAG,
        "onCreateExistTransaction: ${service.addTransaction(transaction) == TransactionResponse.TRANSACTION_ALREADY_EXIST}"
    )
    Log.d(
        TAG,
        "onUpdateTransaction: ${service.updateTransaction(transaction) == TransactionResponse.TRANSACTION_UPDATED}"
    )
    Log.d(
        TAG,
        "onDeleteTransaction: ${service.deleteTransaction(transaction.id) == TransactionResponse.TRANSACTION_DELETED}"
    )
}

private fun <T : Category> checkCategoryService(category: T, service: CategoryActions<T>) {
    Log.d(TAG, "onHasNoCategory: ${!service.hasCategory(category.id)}")
    Log.d(TAG, "onGetNoCategory: ${service.getCategory(category.id) == null}")
    Log.d(
        TAG,
        "onUpdateNoCategory: ${service.updateCategory(category) == CategoryResponse.CATEGORY_NOT_EXIST}"
    )
    Log.d(
        TAG,
        "onDeleteNoCategory: ${service.deleteCategory(category.id) == CategoryResponse.CATEGORY_NOT_EXIST}"
    )

    Log.d(
        TAG,
        "onCreateCategory: ${service.addCategory(category) == CategoryResponse.CATEGORY_CREATED}"
    )
    Log.d(TAG, "onHasCategory: ${service.hasCategory(category.id)}")
    Log.d(TAG, "onGetCategory: ${service.getCategory(category.id) != null}")
    Log.d(
        TAG,
        "onCreateExistCategory: ${service.addCategory(category) == CategoryResponse.CATEGORY_ALREADY_EXIST}"
    )
    Log.d(
        TAG,
        "onUpdateCategory: ${service.updateCategory(category) == CategoryResponse.CATEGORY_UPDATED}"
    )
    Log.d(
        TAG,
        "onDeleteCategory: ${service.deleteCategory(category.id) == CategoryResponse.CATEGORY_DELETED}"
    )
}

private fun <T : Account> checkAccountService(account: T, service: AccountActions<T>) {
    Log.d(TAG, "onHasNoAccount: ${!service.hasAccount(account.id)}")
    Log.d(TAG, "onGetNoAccount: ${service.getAccount(account.id) == null}")
    Log.d(
        TAG,
        "onUpdateNoAccount: ${service.updateAccount(account) == AccountResponse.ACCOUNT_NOT_EXIST}"
    )
    Log.d(
        TAG,
        "onDeleteNoAccount: ${service.deleteAccount(account.id) == AccountResponse.ACCOUNT_NOT_EXIST}"
    )

    Log.d(
        TAG,
        "onCreateAccount: ${service.addAccount(account) == AccountResponse.ACCOUNT_CREATED}"
    )
    Log.d(TAG, "onHasAccount: ${service.hasAccount(account.id)}")
    Log.d(TAG, "onGetAccount: ${service.getAccount(account.id) != null}")
    Log.d(
        TAG,
        "onCreateExistAccount: ${service.addAccount(account) == AccountResponse.ACCOUNT_ALREADY_EXIST}"
    )
    Log.d(
        TAG,
        "onUpdateAccount: ${service.updateAccount(account) == AccountResponse.ACCOUNT_UPDATED}"
    )
    Log.d(
        TAG,
        "onDeleteAccount: ${service.deleteAccount(account.id) == AccountResponse.ACCOUNT_DELETED}"
    )
}