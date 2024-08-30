package com.expensetracker.domain.contracts.account

import com.expensetracker.core.models.Account
import com.expensetracker.core.models.AccountID
import com.expensetracker.core.models.BankAccount
import com.expensetracker.core.models.CashAccount
import com.expensetracker.core.models.CreditCard
import com.expensetracker.core.models.DebitCard

interface AccountProvider {
    val accounts: List<Account>
    val bankAccounts: List<BankAccount>
    val cashAccounts: List<CashAccount>
    val creditCards: List<CreditCard>
    val debitCards: List<DebitCard>
    fun hasAccount(accountID: AccountID): Boolean
}