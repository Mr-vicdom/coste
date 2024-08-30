package com.expensetracker.core.actions


import com.expensetracker.core.models.Account
import com.expensetracker.core.models.AccountID
import com.expensetracker.core.support.AccountResponse

interface AccountActions<T: Account> {
    fun generateId(): AccountID
    fun addAccount(account: T): AccountResponse
    fun hasAccount(accountID: AccountID): Boolean
    fun getAccount(accountID: AccountID): T?
    fun getAllAccounts(): List<T>
    fun updateAccount(account: T): AccountResponse
    fun deleteAccount(id: AccountID): AccountResponse
}