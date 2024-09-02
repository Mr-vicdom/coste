package com.expensetracker.app.transactions.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.app.R
import com.expensetracker.app.accounts.AccountActivity
import com.expensetracker.app.data.DataHandler
import com.expensetracker.app.databinding.TransactionsScreenBinding
import com.expensetracker.app.support.DataGenerator
import com.expensetracker.app.transactions.support.Literals.FILTER_ACCOUNT_IDS_LABEL
import com.expensetracker.app.transactions.support.Literals.MONTH_LABEL
import com.expensetracker.app.transactions.support.Literals.TRANSACTION_ID_LABEL
import com.expensetracker.app.transactions.support.Literals.YEAR_LABEL
import com.expensetracker.app.transactions.viewmodel.TransactionProviderViewModel
import com.expensetracker.core.models.AccountID
import com.expensetracker.core.models.Transaction
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import kotlin.math.log

const val TAG = "TransactionActivity=>log"

class TransactionsActivity: AppCompatActivity() {


    private val dataHandler by lazy { DataHandler(this) }

    private val transactionProviderViewModel: TransactionProviderViewModel by viewModels()
    private lateinit var binding: TransactionsScreenBinding

    private var transactionTotalIncome: Double = 0.0
    private var transactionTotalExpense: Double = 0.0
    private val transactionTotalBalance: Double
        get() = transactionTotalIncome - transactionTotalExpense

    private val filterAccounts: MutableList<AccountID> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)

        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG, "onCreate: ${dataHandler.accountProvider.accounts}")
            transactionProviderViewModel.fetchTransactionsBetween()
        }

        savedInstanceState?.getIntArray(FILTER_ACCOUNT_IDS_LABEL)?.let {
            filterAccounts.clear()
            filterAccounts.addAll(it.toList())
        }

        binding = TransactionsScreenBinding.inflate(layoutInflater)


        val transactionScreenSearchBtn = binding.transScreenSearchBtn
        val transactionScreenCloseFilterBtn = binding.transScreenCloseFilterBtn
        val transScreenFilterBtn = binding.transScreenFilterBtn
        val transactionListView : RecyclerView = binding.transScreenListView

        val addTransactionLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){
                transactionProviderViewModel.fetchTransactionsBetween(filterIDs = filterAccounts)
            }
        }

        val adapter = transactionProviderViewModel.getTransactionListAdapter { transaction: Transaction ->
            val modifyTransactionIntent = Intent(this,TransactionModifyActivity::class.java)
            modifyTransactionIntent.putExtra(TRANSACTION_ID_LABEL,transaction.id)
            addTransactionLauncher.launch(modifyTransactionIntent)
        }

        val filterTransactionLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){

                result.data?.getIntArrayExtra(FILTER_ACCOUNT_IDS_LABEL)?.let {
                    filterAccounts.clear()
                    filterAccounts.addAll(it.toList())
                    transactionScreenSearchBtn.visibility = View.GONE
                    transactionScreenCloseFilterBtn.visibility = View.VISIBLE
                    updateStatusBar()
                }

                transactionProviderViewModel.fetchTransactionsBetween(filterIDs = filterAccounts)
            }
        }


        transactionListView.adapter = adapter
        transactionListView.layoutManager = LinearLayoutManager(this)
        transactionListView.post {
            transactionProviderViewModel.fetchTransactionsBetween(filterIDs = filterAccounts)
        }

        //Search BTN
        transactionScreenSearchBtn.setOnClickListener {
            startActivity(Intent(this,TransactionSearchActivity::class.java))
        }

        if(filterAccounts.isNotEmpty()){
            transactionScreenSearchBtn.visibility = View.GONE
            transactionScreenCloseFilterBtn.visibility = View.VISIBLE
            updateStatusBar()
        } else {
            transactionScreenSearchBtn.visibility = View.VISIBLE
            transactionScreenCloseFilterBtn.visibility = View.GONE
        }

        //Filter Cancel BTN
        transactionScreenCloseFilterBtn.setOnClickListener {
            filterAccounts.clear()
            //back status bar color
            transactionScreenSearchBtn.visibility = View.VISIBLE
            transactionScreenCloseFilterBtn.visibility = View.GONE
            transactionProviderViewModel.fetchTransactionsBetween()
            updateStatusBar()
        }

        //Filter BTN
        transScreenFilterBtn.setOnClickListener{
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
            binding.transInfo1.text = transactionTotalIncome.toString()
            binding.transInfo3.text = transactionTotalBalance.toString()
        }
        transactionProviderViewModel.totalExpense.observe(this) {
            transactionTotalExpense = it
            binding.transInfo2.text = it.toString()
            binding.transInfo3.text = transactionTotalBalance.toString()
        }
        //BOTTOM NAV BAR
        binding.transBottomNavBar.setOnItemSelectedListener {
            Log.d(TAG, "onCreate: ${it.itemId} ${binding.transBottomNavBar.findViewById<BottomNavigationItemView>(it.itemId)}")
            if(binding.transBottomNavBar.menu.size() > 2 && it.itemId == binding.transBottomNavBar.menu.getItem(2).itemId){
                val intent = Intent(this, AccountActivity::class.java)
                startActivity(intent)
            }
            return@setOnItemSelectedListener true
        }

        setContentView(binding.root)
    }

    private fun updateStatusBar() {
        val colorPrimary = ContextCompat.getColor(this,R.color.colorPrimary)
        var color = MaterialColors.getColor(this, androidx.appcompat.R.attr.colorPrimary, colorPrimary)
        window.statusBarColor = color
        if(filterAccounts.isNotEmpty()){
            color = ContextCompat.getColor(this,R.color.dark_blue_600)
            window.statusBarColor = color
        }
        binding.transScreenAppBar.setBackgroundColor(color)
        binding.transScreenViewBar.setBackgroundColor(color)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putIntArray(FILTER_ACCOUNT_IDS_LABEL,filterAccounts.toIntArray())
    }


}