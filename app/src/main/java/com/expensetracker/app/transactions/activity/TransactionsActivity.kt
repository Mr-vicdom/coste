package com.expensetracker.app.transactions.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.app.R
import com.expensetracker.app.data.DataHandler.accountManager
import com.expensetracker.app.data.DataHandler.accountProvider
import com.expensetracker.app.data.DataHandler.categoryManager
import com.expensetracker.app.data.DataHandler.categoryProvider
import com.expensetracker.app.data.DataHandler.transactionManager
import com.expensetracker.app.databinding.TransactionAppBarBinding
import com.expensetracker.app.databinding.TransactionInfoBinding
import com.expensetracker.app.databinding.TransactionViewBarBinding
import com.expensetracker.app.databinding.TransactionsListBinding
import com.expensetracker.app.databinding.TransactionsScreenBinding
import com.expensetracker.app.support.DataGenerator
import com.expensetracker.app.transactions.support.Literals.FILTER_ACCOUNT_IDS_LABEL
import com.expensetracker.app.transactions.support.Literals.MONTH_LABEL
import com.expensetracker.app.transactions.support.Literals.TRANSACTION_ID_LABEL
import com.expensetracker.app.transactions.support.Literals.YEAR_LABEL
import com.expensetracker.app.transactions.viewmodel.TransactionProviderViewModel
import com.expensetracker.core.models.AccountID
import com.expensetracker.core.models.Transaction
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate

const val TAG = "TransactionActivity=>log"

class TransactionsActivity: AppCompatActivity() {

    init {
        GlobalScope.launch {
            DataGenerator.generateDefaultAccounts(accountManager)
            DataGenerator.generateDefaultCategories(categoryManager)
            DataGenerator.generateDummyTransactions(transactionManager, categoryProvider, accountProvider)
        }
    }

    private val transactionProviderViewModel: TransactionProviderViewModel by viewModels()
    private lateinit var binding: TransactionsScreenBinding
    private lateinit var appBarBinding: TransactionAppBarBinding
    private lateinit var transactionViewBar: TransactionViewBarBinding

    private var transactionTotalIncome: Double = 0.0
    private var transactionTotalExpense: Double = 0.0
    private val transactionTotalBalance: Double
        get() = transactionTotalIncome - transactionTotalExpense

    private val filterAccounts: MutableList<AccountID> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)

        savedInstanceState?.getIntArray(FILTER_ACCOUNT_IDS_LABEL)?.let {
            filterAccounts.clear()
            filterAccounts.addAll(it.toList())
        }

        binding = TransactionsScreenBinding.inflate(layoutInflater)
        appBarBinding = TransactionAppBarBinding.bind(binding.root)
        transactionViewBar = TransactionViewBarBinding.bind(binding.root)
        val listBinding = TransactionsListBinding.bind(binding.root)
        val transInfo = TransactionInfoBinding.bind(binding.root)


        val addTransactionLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){
                transactionProviderViewModel.fetchTransactionsBetween(filterIDs = filterAccounts)
            }
        }

        val filterTransactionLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){

                result.data?.getIntArrayExtra(FILTER_ACCOUNT_IDS_LABEL)?.let {
                    filterAccounts.clear()
                    filterAccounts.addAll(it.toList())
                    appBarBinding.transactionScreenSearchBtn1.visibility = View.GONE
                    appBarBinding.transactionScreenCancelFilterBtn.visibility = View.VISIBLE
                    updateStatusBar()
                }

                transactionProviderViewModel.fetchTransactionsBetween(filterIDs = filterAccounts)
            }
        }

        val transactionListView : RecyclerView = listBinding.transactionsListView
        val adapter = transactionProviderViewModel.getTransactionListAdapter { transaction: Transaction ->
            val modifyTransactionIntent = Intent(this,TransactionModifyActivity::class.java)
            modifyTransactionIntent.putExtra(TRANSACTION_ID_LABEL,transaction.id)
            addTransactionLauncher.launch(modifyTransactionIntent)
        }

        transactionListView.adapter = adapter
        transactionListView.layoutManager = LinearLayoutManager(this)
        transactionListView.post {
            transactionProviderViewModel.fetchTransactionsBetween(filterIDs = filterAccounts)
        }

        //Search BTN
        appBarBinding.transactionScreenSearchBtn1.setOnClickListener {
            startActivity(Intent(this,TransactionSearchActivity::class.java))
        }

        if(filterAccounts.isNotEmpty()){
            appBarBinding.transactionScreenSearchBtn1.visibility = View.GONE
            appBarBinding.transactionScreenCancelFilterBtn.visibility = View.VISIBLE
            updateStatusBar()
        } else {
            appBarBinding.transactionScreenSearchBtn1.visibility = View.VISIBLE
            appBarBinding.transactionScreenCancelFilterBtn.visibility = View.GONE
        }

        //Filter Cancel BTN
        appBarBinding.transactionScreenCancelFilterBtn.setOnClickListener {
            filterAccounts.clear()
            //back status bar color
            appBarBinding.transactionScreenSearchBtn1.visibility = View.VISIBLE
            appBarBinding.transactionScreenCancelFilterBtn.visibility = View.GONE
            transactionProviderViewModel.fetchTransactionsBetween()
            updateStatusBar()
        }

        //Filter BTN
        appBarBinding.transactionScreenFilterBtn1.setOnClickListener{
            val filterIntent = Intent(this, TransactionFilterActivity::class.java)
            filterIntent.putExtra(MONTH_LABEL,LocalDate.now().monthValue)
            filterIntent.putExtra(YEAR_LABEL,LocalDate.now().year)
            filterIntent.putExtra(FILTER_ACCOUNT_IDS_LABEL,filterAccounts.toIntArray())
            filterTransactionLauncher.launch(filterIntent)
        }

        //Floating BTN
        binding.floatingBtn.setOnClickListener {
            val transAddIntent: Intent = Intent(this,TransactionModifyActivity::class.java)
            addTransactionLauncher.launch(transAddIntent)
        }

        transactionProviderViewModel.totalIncome.observe(this) {
            transactionTotalIncome = it
            transInfo.transInfo1.text = transactionTotalIncome.toString()
            transInfo.transInfo3.text = transactionTotalBalance.toString()
        }
        transactionProviderViewModel.totalExpense.observe(this) {
            transactionTotalExpense = it
            transInfo.transInfo2.text = it.toString()
            transInfo.transInfo3.text = transactionTotalBalance.toString()
        }

        setContentView(binding.root)
    }

    private fun updateStatusBar() {
        if(filterAccounts.isNotEmpty()){
            val color = ContextCompat.getColor(this,R.color.dark_blue_600)
            window.statusBarColor = color
            appBarBinding.transactionAppBar.setBackgroundColor(color)
            transactionViewBar.transactionScreenViewTabBar.setBackgroundColor(color)
        } else {
            val colorPrimary = ContextCompat.getColor(this,R.color.colorPrimary)
            val color = MaterialColors.getColor(this, androidx.appcompat.R.attr.colorPrimary, colorPrimary)
            window.statusBarColor = color
            appBarBinding.transactionAppBar.setBackgroundColor(color)
            transactionViewBar.transactionScreenViewTabBar.setBackgroundColor(color)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putIntArray(FILTER_ACCOUNT_IDS_LABEL,filterAccounts.toIntArray())
    }


}