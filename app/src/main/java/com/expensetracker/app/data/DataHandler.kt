package com.expensetracker.app.data

import com.expensetracker.core.actions.BankAccountActions
import com.expensetracker.core.actions.CashAccountActions
import com.expensetracker.core.actions.CreditCardActions
import com.expensetracker.core.actions.DebitCardActions
import com.expensetracker.core.actions.ExpenseActions
import com.expensetracker.core.actions.ExpenseCategoryActions
import com.expensetracker.core.actions.IncomeActions
import com.expensetracker.core.actions.IncomeCategoryActions
import com.expensetracker.core.actions.TransferActions
import com.expensetracker.data.datastores.SimpleDataStore
import com.expensetracker.data.services.accounts.BankAccountService
import com.expensetracker.data.services.accounts.CashAccountService
import com.expensetracker.data.services.accounts.CreditCardService
import com.expensetracker.data.services.accounts.DebitCardService
import com.expensetracker.data.services.category.ExpenseCategoryService
import com.expensetracker.data.services.category.IncomeCategoryService
import com.expensetracker.data.services.support.IdGenerator
import com.expensetracker.data.services.transactions.ExpenseService
import com.expensetracker.data.services.transactions.IncomeService
import com.expensetracker.data.services.transactions.TransferService
import com.expensetracker.domain.concretes.account.AccountManagerImp
import com.expensetracker.domain.concretes.account.AccountProviderImp
import com.expensetracker.domain.concretes.category.CategoryManagerImp
import com.expensetracker.domain.concretes.category.CategoryProviderImp
import com.expensetracker.domain.concretes.transaction.TransactionManagerImp
import com.expensetracker.domain.concretes.transaction.TransactionProviderImp
import com.expensetracker.domain.contracts.account.AccountProvider
import com.expensetracker.domain.contracts.category.CategoryManager
import com.expensetracker.domain.contracts.category.CategoryProvider
import com.expensetracker.domain.contracts.transaction.TransactionManager
import com.expensetracker.domain.contracts.transaction.TransactionProvider

object DataHandler {
    private val accountIdGenerator: IdGenerator by lazy { IdGenerator() }
    private val categoryIdGenerator: IdGenerator by lazy { IdGenerator() }
    private val transactionIdGenerator: IdGenerator by lazy { IdGenerator() }

    private val bankAccountActions: BankAccountActions by lazy { BankAccountService(SimpleDataStore.bankAccounts,accountIdGenerator) }
    private val cashAccountActions: CashAccountActions by lazy { CashAccountService(SimpleDataStore.cashAccounts,accountIdGenerator) }
    private val creditCardActions: CreditCardActions by lazy { CreditCardService(SimpleDataStore.creditCards,accountIdGenerator) }
    private val debitCardActions: DebitCardActions by lazy { DebitCardService(SimpleDataStore.debitCards,accountIdGenerator) }

    private val incomeCategoryActions: IncomeCategoryActions by lazy { IncomeCategoryService(SimpleDataStore.incomeCategories,categoryIdGenerator) }
    private val expenseCategoryActions: ExpenseCategoryActions by lazy { ExpenseCategoryService(SimpleDataStore.expenseCategories,categoryIdGenerator) }

    val incomeActions: IncomeActions by lazy { IncomeService(SimpleDataStore.incomes,transactionIdGenerator) }
    private val expenseActions: ExpenseActions by lazy { ExpenseService(SimpleDataStore.expenses,transactionIdGenerator) }
    private val transferActions: TransferActions by lazy { TransferService(SimpleDataStore.transfers,transactionIdGenerator) }

    val accountProvider: AccountProvider by lazy {  AccountProviderImp(bankAccountActions, cashAccountActions, creditCardActions, debitCardActions) }
    val accountManager: AccountManagerImp by lazy {
        AccountManagerImp(
            accountProvider,
            bankAccountActions,
            cashAccountActions,
            creditCardActions,
            debitCardActions
        )
    }

    val categoryProvider: CategoryProvider by lazy {  CategoryProviderImp(incomeCategoryActions, expenseCategoryActions) }
    val categoryManager: CategoryManager by lazy {  CategoryManagerImp(categoryProvider, incomeCategoryActions, expenseCategoryActions) }

    val transactionProvider: TransactionProvider by lazy {
        TransactionProviderImp(
            incomeActions, expenseActions, transferActions
        )
    }
    val transactionManager: TransactionManager by lazy {
        TransactionManagerImp(
            transactionProvider, incomeActions, expenseActions, transferActions, accountManager
        )
    }

}