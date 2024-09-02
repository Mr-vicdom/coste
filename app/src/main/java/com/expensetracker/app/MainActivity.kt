package com.expensetracker.app

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.data_sqlite.DatabaseHelper
import com.expensetracker.data_sqlite.DatabaseSchema
import com.expensetracker.data_sqlite.services.accounts.BankAccountService
import com.expensetracker.data_sqlite.services.accounts.CashAccountService
import com.expensetracker.data_sqlite.services.accounts.CreditCardService
import com.expensetracker.data_sqlite.services.accounts.DebitCardService
import com.expensetracker.data_sqlite.services.category.ExpenseCategoryService
import com.expensetracker.data_sqlite.services.category.IncomeCategoryService
import com.expensetracker.data_sqlite.services.transactions.TransferService
import com.expensetracker.app.databinding.TransactionsScreenBinding
import com.expensetracker.app.support.DataGenerator
import com.expensetracker.app.transactions.viewmodel.TAG
import com.expensetracker.app.transactions.viewmodel.TransactionProviderViewModel
import com.expensetracker.core.actions.AccountActions
import com.expensetracker.core.actions.CategoryActions
import com.expensetracker.core.actions.TransactionActions
import com.expensetracker.core.models.Account
import com.expensetracker.core.models.Category
import com.expensetracker.core.models.Transaction
import com.expensetracker.core.models.Transfer
import com.expensetracker.core.support.AccountResponse
import com.expensetracker.core.support.Amount
import com.expensetracker.core.support.CategoryResponse
import com.expensetracker.core.support.Helper
import com.expensetracker.core.support.SimpleName
import com.expensetracker.core.support.TransactionDescription
import com.expensetracker.core.support.TransactionResponse
import com.expensetracker.data_sqlite.services.support.IdGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        val binding = TransactionsScreenBinding.inflate(layoutInflater)
        val viewModel: TransactionProviderViewModel by viewModels<TransactionProviderViewModel>()

        binding.transScreenListView.adapter = viewModel.getTransactionListAdapter {}
        binding.transScreenListView.layoutManager = LinearLayoutManager(this)
        viewModel.fetchTransactionsBetween()


        val dbHelper = DatabaseHelper(applicationContext)
        val db = dbHelper.writableDatabase

        val bankAccountService =
            BankAccountService(db, IdGenerator(), DatabaseSchema.BankAccountTable)
        val cashAccountService =
            CashAccountService(db, IdGenerator(), DatabaseSchema.CashAccountTable)
        val creditCardService = CreditCardService(db, IdGenerator(), DatabaseSchema.CreditCardTable)
        val debitCardService =
            DebitCardService(db, IdGenerator(), DatabaseSchema.DebitCardTable, bankAccountService)

        val categoryIdGenerator = IdGenerator()
        val incomeCategoryService =
            IncomeCategoryService(db, categoryIdGenerator, DatabaseSchema.CategoryTable)
        val expenseCategoryService =
            ExpenseCategoryService(db, categoryIdGenerator, DatabaseSchema.CategoryTable)

        setContentView(binding.root)

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
}