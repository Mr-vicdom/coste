package com.expensetracker.data.services.accounts

import com.expensetracker.core.actions.AccountActions
import com.expensetracker.core.models.AccountID
import com.expensetracker.core.models.Account
import com.expensetracker.core.support.AccountResponse
import com.expensetracker.data.services.support.IdGenerator

abstract class AccountService<T: Account>(
    private val accounts: MutableMap<AccountID, T>,
    private val idGenerator: IdGenerator
): AccountActions<T> {

    override fun generateId(): AccountID = idGenerator.newId

     override fun addAccount(account: T): AccountResponse {
        if(accounts.contains(account.id)) return AccountResponse.ACCOUNT_ALREADY_EXIST
        accounts[account.id] = account
        return AccountResponse.ACCOUNT_CREATED
    }

    override fun hasAccount(accountID: AccountID): Boolean = accounts.contains(accountID)

     override fun getAccount(accountID: AccountID): T? = accounts[accountID]

     override fun getAllAccounts(): List<T> = accounts.values.toList()

     override fun updateAccount( account: T): AccountResponse {
        if(accounts.contains(account.id)){
            accounts[account.id] = account
            return AccountResponse.ACCOUNT_UPDATED
        } else return AccountResponse.ACCOUNT_NOT_EXIST
    }

     override fun deleteAccount(id: AccountID): AccountResponse {
        return accounts.remove(id).let {
            if(it == null)
                AccountResponse.ACCOUNT_NOT_EXIST
            else
                AccountResponse.ACCOUNT_DELETED
        }
    }
}