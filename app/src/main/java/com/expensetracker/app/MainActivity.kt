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

}