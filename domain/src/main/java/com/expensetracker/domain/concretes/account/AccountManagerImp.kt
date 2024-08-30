package com.expensetracker.domain.concretes.account

import com.expensetracker.core.actions.BankAccountActions
import com.expensetracker.core.actions.CashAccountActions
import com.expensetracker.core.actions.CreditCardActions
import com.expensetracker.core.actions.DebitCardActions
import com.expensetracker.core.models.Account
import com.expensetracker.core.models.BankAccount
import com.expensetracker.core.models.CashAccount
import com.expensetracker.core.models.CreditCard
import com.expensetracker.core.models.DebitCard
import com.expensetracker.core.support.AccountResponse
import com.expensetracker.core.support.Amount
import com.expensetracker.core.support.CustomException
import com.expensetracker.core.support.SimpleName
import com.expensetracker.domain.contracts.account.AccountManager
import com.expensetracker.domain.contracts.account.AccountProvider
import com.expensetracker.domain.support.Result


class AccountManagerImp(
    private val accountProvider: AccountProvider,
    private val bankAccountActions: BankAccountActions,
    private val cashAccountActions: CashAccountActions,
    private val creditCardActions: CreditCardActions,
    private val debitCardActions: DebitCardActions,
): AccountManager,
AccountProvider by accountProvider {

    private fun createAccount(account: Account): Result {
        val result = when (account) {
            is CreditCard -> creditCardActions.addAccount(account)
            is DebitCard -> debitCardActions.addAccount(account)
            is BankAccount -> bankAccountActions.addAccount(account)
            is CashAccount -> cashAccountActions.addAccount(account)
        }

        return if (result == AccountResponse.ACCOUNT_CREATED) {
            Result.Success(result.toString())
        } else Result.Failure(result.toString())
    }

    override fun createBankAccount(
        name: String,
        balance: String,
        minimumBalance: String,
    ): Result {
        return try {
            val balance1: Amount = Amount(balance)
            val name1: SimpleName = SimpleName(name)
            val minimumBalance1: Amount = Amount(minimumBalance)
            return createAccount(
                BankAccount(
                    bankAccountActions.generateId(), name1, balance1, minimumBalance1
                )
            )
        } catch (e: CustomException) {
            Result.Failure(e.message)
        }
    }

    override fun createCashAccount(
        name: String,
        balance: String,
        minimumBalance: String,
    ): Result {
        return try {
            val balance1: Amount = Amount(balance)
            val name1: SimpleName = SimpleName(name)
            val minimumBalance1: Amount = Amount(minimumBalance)
            return createAccount(
                CashAccount(
                    cashAccountActions.generateId(), name1, balance1, minimumBalance1
                )
            )
        } catch (e: CustomException) {
            Result.Failure(e.message)
        }
    }

    override fun createCreditCard(
        name: String,
        balance: String,
        outStandings: String,
    ): Result {
        return try {
            val balance1: Amount = Amount(balance)
            val name1: SimpleName = SimpleName(name)
            val outStandings1: Amount = Amount(outStandings)
            return createAccount(
                CreditCard(
                    creditCardActions.generateId(), name1, balance1, outStandings1
                )
            )
        } catch (e: CustomException) {
            Result.Failure(e.message)
        }
    }

    override fun createDebitCard(
        name: String,
        bankAccount: BankAccount,
        withdrawLimit: String,
        balance: String
    ): Result {
        return try {
            val balance1: Amount = Amount(balance)
            val name1: SimpleName = SimpleName(name)
            val withdrawLimit1: Amount = Amount(withdrawLimit)
            return createAccount(
                DebitCard(
                    debitCardActions.generateId(), bankAccount, name1, balance1, withdrawLimit1
                )
            )
        } catch (e: CustomException) {
            Result.Failure(e.message)
        }
    }

    override fun updateBankAccount(
        bankAccount: BankAccount,
        name: String?,
        balance: String?,
        minimumBalance: String?
    ): Result {
        var updatedAccount = bankAccount.copy()
        name?.let { updatedAccount = bankAccount.copy(name = SimpleName(it)) }
        balance?.let { updatedAccount = bankAccount.copy(balance = Amount(it)) }
        minimumBalance?.let { updatedAccount = bankAccount.copy(minimumBalance = Amount(it)) }
        val accountResponse = updateAccount(updatedAccount)
        return if(accountResponse == AccountResponse.ACCOUNT_UPDATED){
            Result.Success(accountResponse.toString())
        } else Result.Failure(accountResponse.toString())
    }

    override fun updateCashAccount(
        cashAccount: CashAccount,
        name: String?,
        balance: String?,
        minimumBalance: String?
    ): Result {
        var updatedAccount = cashAccount.copy()
        name?.let { updatedAccount = cashAccount.copy(name = SimpleName(it)) }
        balance?.let { updatedAccount = cashAccount.copy(balance = Amount(it)) }
        minimumBalance?.let { updatedAccount = cashAccount.copy(minimumBalance = Amount(it)) }
        val accountResponse = updateAccount(updatedAccount)
        return if(accountResponse == AccountResponse.ACCOUNT_UPDATED){
            Result.Success(accountResponse.toString())
        } else Result.Failure(accountResponse.toString())
    }

    override fun updateCreditCard(
        creditCard: CreditCard,
        name: String?,
        balance: String?,
        outStandings: String?
    ): Result {
        var updatedAccount = creditCard.copy()
        name?.let { updatedAccount = creditCard.copy(name = SimpleName(it)) }
        balance?.let { updatedAccount = creditCard.copy(balance = Amount(it)) }
        outStandings?.let { updatedAccount = creditCard.copy(outStandings = Amount(it)) }
        val accountResponse = updateAccount(updatedAccount)
        return if(accountResponse == AccountResponse.ACCOUNT_UPDATED){
            Result.Success(accountResponse.toString())
        } else Result.Failure(accountResponse.toString())
    }

    override fun updateDebitCard(
        debitCard: DebitCard,
        name: String?,
        balance: String?,
        bankAccount: BankAccount?,
        withdrawLimit: String?
    ): Result {
        var updatedAccount = debitCard.copy()
        name?.let { updatedAccount = debitCard.copy(name = SimpleName(it)) }
        balance?.let { updatedAccount = debitCard.copy(balance = Amount(it)) }
        bankAccount?.let { updatedAccount = debitCard.copy(bankAccount = it) }
        withdrawLimit?.let { updatedAccount = debitCard.copy(limit = Amount(it)) }
        val accountResponse = updateAccount(updatedAccount)
        return if(accountResponse == AccountResponse.ACCOUNT_UPDATED){
            Result.Success(accountResponse.toString())
        } else Result.Failure(accountResponse.toString())
    }

    fun creditAccount(account: Account, amount: Amount) : Result {
        val accountResponse = when(account){
            is BankAccount -> creditBankAccount(account,amount)
            is CashAccount -> creditCashAccount(account,amount)
            is CreditCard -> creditCreditCard(account,amount)
            is DebitCard -> creditDebitCard(account,amount)
        }

        return if(accountResponse == AccountResponse.ACCOUNT_UPDATED) {
            Result.Success(accountResponse.toString())
        } else Result.Failure(accountResponse.toString())
    }

    fun debitAccount(account: Account, amount: Amount) : Result {
        val accountResponse = when(account){
            is BankAccount -> debitBankAccount(account,amount)
            is CashAccount -> debitCashAccount(account,amount)
            is CreditCard -> debitCreditCard(account,amount)
            is DebitCard -> debitDebitCard(account,amount)
        }

        return if(accountResponse == AccountResponse.ACCOUNT_UPDATED) {
            Result.Success(accountResponse.toString())
        } else Result.Failure(accountResponse.toString())
    }

    private fun updateAccount(account: Account): AccountResponse {
        return when (account) {
            is CreditCard -> creditCardActions.updateAccount(account)
            is DebitCard -> debitCardActions.updateAccount(account)
            is BankAccount -> bankAccountActions.updateAccount(account)
            is CashAccount -> cashAccountActions.updateAccount(account)
        }
    }

    override fun deleteAccount(account: Account): Result {
        return when (account) {
            is CreditCard -> creditCardActions.deleteAccount(account.id)
            is DebitCard -> debitCardActions.deleteAccount(account.id)
            is BankAccount -> bankAccountActions.deleteAccount(account.id)
            is CashAccount -> cashAccountActions.deleteAccount(account.id)
        }.let {
            if (it == AccountResponse.ACCOUNT_DELETED){
                Result.Success(it.toString())
            } else Result.Failure(it.toString())
        }
    }


    private fun creditBankAccount(account: BankAccount, creditAmount: Amount): AccountResponse {
        val currentBalance: Amount = account.balance + creditAmount
        val updatedAccount = account.copy(balance = currentBalance)
        return this.updateAccount(updatedAccount)
    }


    private fun creditCashAccount(account: CashAccount, creditAmount: Amount): AccountResponse {
        val currentBalance: Amount = account.balance + creditAmount
        val updatedAccount = account.copy(balance = currentBalance)
        return this.updateAccount(updatedAccount)
    }

    private fun creditCreditCard(account: CreditCard, creditAmount: Amount): AccountResponse {
        val currentBalance: Amount = account.balance + creditAmount
        val updatedAccount = account.copy(balance = currentBalance)
        return this.updateAccount(updatedAccount)
    }

    private fun creditDebitCard(account: DebitCard, creditAmount: Amount): AccountResponse {
        return creditBankAccount(account.bankAccount, creditAmount)
    }


    private fun debitBankAccount(account: BankAccount, debitAmount: Amount) : AccountResponse {
        val currentBalance: Amount = account.balance - debitAmount
        val updatedAccount = account.copy(balance = currentBalance)
        return this.updateAccount(updatedAccount)
    }

    private fun debitCashAccount(account: CashAccount, debitAmount: Amount) : AccountResponse {
        val currentBalance: Amount = account.balance - debitAmount
        val updatedAccount = account.copy(balance = currentBalance)
        return this.updateAccount(updatedAccount)
    }

    private fun debitCreditCard(account: CreditCard, debitAmount: Amount) : AccountResponse {
        val currentBalance: Amount = account.balance - debitAmount
        val updatedAccount = account.copy(balance = currentBalance)
        return this.updateAccount(updatedAccount)
    }

    private fun debitDebitCard(account: DebitCard, debitAmount: Amount) : AccountResponse {
        return debitBankAccount(account.bankAccount,debitAmount)
    }


}