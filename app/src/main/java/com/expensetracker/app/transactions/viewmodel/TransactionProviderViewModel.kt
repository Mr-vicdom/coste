package com.expensetracker.app.transactions.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.data.DataHandler
import com.expensetracker.app.transactions.support.PeriodicDataByDay
import com.expensetracker.app.transactions.support.TransactionItems
import com.expensetracker.app.transactions.adapter.TransactionsListAdapter
import com.expensetracker.core.models.AccountID
import com.expensetracker.core.models.Expense
import com.expensetracker.core.models.FinancialTransaction
import com.expensetracker.core.models.Income
import com.expensetracker.core.models.Transaction
import com.expensetracker.core.models.Transfer
import com.expensetracker.core.support.Helper
import com.expensetracker.domain.contracts.transaction.TransactionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth

const val TAG = "TransactionsViewModel=>log"

class TransactionProviderViewModel(application: Application): AndroidViewModel(application) {

    private val transactionProvider: TransactionProvider by lazy {
        DataHandler(application).transactionProvider
    }



    private val _transactions: MutableList<Transaction> by lazy {
        mutableListOf()
    }
    private val offset: Long = 0
    private val limit: Long = 30
    private val _totalIncome: MutableLiveData<Double> = MutableLiveData(0.0)
    private val _totalExpense: MutableLiveData<Double> = MutableLiveData(0.0)
    private val transactionItems : MutableList<TransactionItems> = mutableListOf()
    private lateinit var transactionsListAdapter: TransactionsListAdapter


    init {
        viewModelScope.launch(Dispatchers.IO) {
            delay(3000)
            month = Month.AUGUST
        }
    }

    var month: Month = LocalDate.now().month
        set(value) {
            field = value
            fetchTransactionsBetween()
        }

    var year: Year = Year.now()
        set(value) {
            field = value
            fetchTransactionsBetween()
        }

    val totalIncome: LiveData<Double>
        get() = _totalIncome
    val totalExpense: LiveData<Double>
        get() = _totalExpense


    fun getTransactionListAdapter(onItemClickListener: (Transaction) -> Unit) : TransactionsListAdapter {
        transactionsListAdapter = TransactionsListAdapter(transactionItems, onItemClickListener)
        return transactionsListAdapter
    }


    fun fetchTransactionsBetween( filterIDs: List<AccountID> = listOf()) {
        val from: LocalDate = YearMonth.of(year.value,month).atEndOfMonth()
        val to: LocalDate = LocalDate.of(year.value,month,1)

        _transactions.clear()
        viewModelScope.launch(Dispatchers.IO) {
            val data = transactionProvider.getTransactionsBetween(from, to) {
                if(filterIDs.isEmpty()) true
                else when(it){
                    is FinancialTransaction -> filterIDs.contains(it.account.id)
                    is Transfer -> filterIDs.contains(it.fromAccount.id) || filterIDs.contains(it.toAccount.id)
                }
            }

            Log.d(TAG, "$data: ${filterIDs.isEmpty()}")

            withContext(Dispatchers.Main){
                _transactions.addAll(data)
                prepareTransactionItemsForDay()
                transactionsListAdapter.notifyDataSetChanged()

                Log.d(TAG, "fetchTransactionsBetween $from and $to: ${_transactions.size}")
            }

        }
    }

    fun fetchTransactionsMatches(from: LocalDate = LocalDate.now(), to: LocalDate = LocalDate.now().withDayOfMonth(1), query: String = "") {
        _transactions.clear()
        viewModelScope.launch {
            val data = transactionProvider.getTransactions(offset, limit) {
                if(query.isEmpty()) return@getTransactions false
                it.note.toString().lowercase().contains(query.lowercase())
            }
            _transactions.addAll(data)
            prepareTransactionItemsForDay()
            transactionsListAdapter.notifyDataSetChanged()

            Log.d(TAG, "Filter fetchTransactions $from and $to: ${_transactions.size}")
        }
    }

    private fun prepareTransactionItemsForDay() {
        transactionItems.clear()
        var totalIncome1 = 0.0
        var totalExpense1 = 0.0
        _transactions.groupBy { it.date }.forEach { (date, values) ->
            val totalPeriodicIncome: Double = values.filterIsInstance<Income>().sumOf { it.amount.toString().toDouble() }
            val totalPeriodicExpense: Double = values.filterIsInstance<Expense>().sumOf { it.amount.toString().toDouble() }
            totalIncome1 += totalPeriodicIncome
            totalExpense1 += totalPeriodicExpense
            val periodicData = PeriodicDataByDay(Helper.millisToDate(date),totalPeriodicIncome.toString(),totalPeriodicExpense.toString())
            transactionItems.add(TransactionItems.PeriodicItem(periodicData))
            values.sortedByDescending { it.id }.forEach { transactionItems.add(TransactionItems.TransactionItem(it)) }
        }
        _totalIncome.postValue(totalIncome1)
        _totalExpense.postValue(totalExpense1)
    }

}