package com.expensetracker.app

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.app.data.DataHandler.accountManager
import com.expensetracker.app.data.DataHandler.accountProvider
import com.expensetracker.app.data.DataHandler.categoryManager
import com.expensetracker.app.data.DataHandler.categoryProvider
import com.expensetracker.app.data.DataHandler.transactionManager
import com.expensetracker.app.databinding.TransactionsScreenCoordinatorBinding
import com.expensetracker.app.support.DataGenerator
import com.expensetracker.app.transactions.fragment.TransactionsList
import com.expensetracker.app.transactions.viewmodel.TransactionProviderViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity: AppCompatActivity() {

    init {
        GlobalScope.launch {
            DataGenerator.generateDefaultAccounts(accountManager)
            DataGenerator.generateDefaultCategories(categoryManager)
            DataGenerator.generateDummyTransactions(transactionManager, categoryProvider, accountProvider)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val binding = TransactionsScreenCoordinatorBinding.inflate(layoutInflater)
        val viewModel: TransactionProviderViewModel by viewModels<TransactionProviderViewModel>()

        binding.transScreenListView.adapter = viewModel.getTransactionListAdapter{}
        binding.transScreenListView.layoutManager = LinearLayoutManager(this)
        viewModel.fetchTransactionsBetween()


        setContentView(binding.root)

    }

}