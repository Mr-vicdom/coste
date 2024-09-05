package com.expensetracker.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.expensetracker.app.support.DataGenerator
import com.expensetracker.core.actions.BankAccountActions
import com.expensetracker.core.actions.CashAccountActions
import com.expensetracker.core.actions.CreditCardActions
import com.expensetracker.core.actions.DebitCardActions
import com.expensetracker.core.actions.ExpenseActions
import com.expensetracker.core.actions.ExpenseCategoryActions
import com.expensetracker.core.actions.IncomeActions
import com.expensetracker.core.actions.IncomeCategoryActions
import com.expensetracker.core.actions.TransferActions
import com.expensetracker.data_sqlite.DatabaseHelper
import com.expensetracker.data_sqlite.DatabaseSchema
import com.expensetracker.data_sqlite.services.accounts.BankAccountService
import com.expensetracker.data_sqlite.services.accounts.CashAccountService
import com.expensetracker.data_sqlite.services.accounts.CreditCardService
import com.expensetracker.data_sqlite.services.accounts.DebitCardService
import com.expensetracker.data_sqlite.services.category.ExpenseCategoryService
import com.expensetracker.data_sqlite.services.category.IncomeCategoryService
import com.expensetracker.data_sqlite.services.support.IdGenerator
import com.expensetracker.data_sqlite.services.support.IdStoreMaster
import com.expensetracker.data_sqlite.services.transactions.ExpenseService
import com.expensetracker.data_sqlite.services.transactions.IncomeService
import com.expensetracker.data_sqlite.services.transactions.TransferService
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

class DataHandler(context: Context) {

    companion object {
        private var writableDatabase: SQLiteDatabase? = null
    }

    private val dbHelper = DatabaseHelper(context)
    private val db : SQLiteDatabase = writableDatabase ?: let {
        dbHelper.writableDatabase
    }


    private val idStoreMaster: IdStoreMaster by lazy { IdStoreMaster(db) }
    private val accountIdGenerator: IdGenerator by lazy { idStoreMaster.forAccount }
    private val categoryIdGenerator: IdGenerator by lazy { idStoreMaster.forCategory }
    private val transactionIdGenerator: IdGenerator by lazy { idStoreMaster.forTransaction }


    private val bankAccountActions: BankAccountActions by lazy { BankAccountService(db, accountIdGenerator, DatabaseSchema.BankAccountTable) }
    private val cashAccountActions: CashAccountActions by lazy { CashAccountService(db, accountIdGenerator, DatabaseSchema.CashAccountTable) }
    private val creditCardActions: CreditCardActions by lazy { CreditCardService(db, accountIdGenerator, DatabaseSchema.CreditCardTable) }
    private val debitCardActions: DebitCardActions by lazy { DebitCardService(db, accountIdGenerator ,DatabaseSchema.DebitCardTable, bankAccountActions) }

    private val incomeCategoryActions: IncomeCategoryActions by lazy { IncomeCategoryService(db, categoryIdGenerator,DatabaseSchema.CategoryTable) }
    private val expenseCategoryActions: ExpenseCategoryActions by lazy { ExpenseCategoryService(db, categoryIdGenerator,DatabaseSchema.CategoryTable) }

    private val incomeActions: IncomeActions by lazy { IncomeService(db, transactionIdGenerator,DatabaseSchema.IncomeTable, incomeCategoryActions, bankAccountActions, cashAccountActions, creditCardActions, debitCardActions) }
    private val expenseActions: ExpenseActions by lazy { ExpenseService(db, transactionIdGenerator,DatabaseSchema.ExpenseTable, expenseCategoryActions, bankAccountActions, cashAccountActions, creditCardActions, debitCardActions) }
    private val transferActions: TransferActions by lazy { TransferService(db, transactionIdGenerator,DatabaseSchema.TransferTable, bankAccountActions, cashAccountActions, creditCardActions, debitCardActions) }

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

    init {
        if (writableDatabase == null) {
            writableDatabase = db
//            DataGenerator.generateDefaultAccounts(accountManager)
//            DataGenerator.generateDefaultCategories(categoryManager)
//            DataGenerator.generateDummyTransactions(transactionManager, categoryProvider, accountProvider)
        }
    }

}