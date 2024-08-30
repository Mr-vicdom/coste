package com.expensetracker.domain.contracts.account

import com.expensetracker.core.models.Account
import com.expensetracker.core.models.BankAccount
import com.expensetracker.core.models.CashAccount
import com.expensetracker.core.models.CreditCard
import com.expensetracker.core.models.DebitCard
import com.expensetracker.domain.support.Result

const val DEFAULT_BALANCE = "0.0"
const val DEFAULT_MINIMUM_BALANCE = "500"
const val DEFAULT_WITHDRAWAL_LIMIT = "50000"

interface AccountManager: AccountProvider {
    fun createBankAccount(name: String, balance: String = DEFAULT_BALANCE, minimumBalance: String = DEFAULT_MINIMUM_BALANCE): Result
    fun createCashAccount(name: String, balance: String = DEFAULT_BALANCE, minimumBalance: String = DEFAULT_MINIMUM_BALANCE): Result
    fun createCreditCard(name: String, balance: String = DEFAULT_BALANCE, outStandings: String = DEFAULT_BALANCE): Result
    fun createDebitCard(name: String, bankAccount: BankAccount, withdrawLimit : String = DEFAULT_WITHDRAWAL_LIMIT, balance: String = DEFAULT_BALANCE): Result
    fun updateBankAccount(bankAccount:BankAccount,name: String? = null, balance: String? = null, minimumBalance: String? = null): Result
    fun updateCashAccount(cashAccount: CashAccount,name: String? = null, balance: String? = null, minimumBalance: String? = null): Result
    fun updateCreditCard(creditCard: CreditCard,name: String? = null, balance: String? = null, outStandings: String? = null): Result
    fun updateDebitCard(debitCard: DebitCard,name: String? = null, balance: String? = null, bankAccount: BankAccount? = null, withdrawLimit : String? = null): Result
    fun deleteAccount(account: Account): Result
}