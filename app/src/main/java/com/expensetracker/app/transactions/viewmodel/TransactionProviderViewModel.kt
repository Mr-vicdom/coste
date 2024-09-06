package com.expensetracker.app.transactions.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.data.DataHandler
import com.expensetracker.app.transactions.support.PeriodicDataByDay
import com.expensetracker.app.transactions.support.PeriodicDataByWeek
import com.expensetracker.app.transactions.support.TransactionItems
import com.expensetracker.app.transactions.support.TransactionItemsByWeek
import com.expensetracker.app.transactions.support.TransactionsViewMode
import com.expensetracker.core.models.AccountID
import com.expensetracker.core.models.Expense
import com.expensetracker.core.models.FinancialTransaction
import com.expensetracker.core.models.Income
import com.expensetracker.core.models.Transaction
import com.expensetracker.core.models.Transfer
import com.expensetracker.core.support.Helper
import com.expensetracker.domain.contracts.transaction.TransactionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth
import java.time.temporal.IsoFields

const val TAG = "TransactionsViewModel=>log"

class TransactionProviderViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionProvider: TransactionProvider by lazy {
        DataHandler(application).transactionProvider
    }

    private val _totalIncome: MutableLiveData<Double> = MutableLiveData(0.0)
    private val _totalExpense: MutableLiveData<Double> = MutableLiveData(0.0)

    private var selectedTransactionsViewMode: TransactionsViewMode = TransactionsViewMode.DAILY

    private val _transactionViewMode: MutableLiveData<TransactionsViewMode> = MutableLiveData()

    private val _transactionItems: MutableLiveData<List<TransactionItems>> = MutableLiveData()

    private val _transactionItemsByWeek: MutableLiveData<List<TransactionItemsByWeek>> =
        MutableLiveData()

    private val filterIDs: MutableSet<Int> = mutableSetOf()

    val transactionsViewMode: LiveData<TransactionsViewMode>
        get() = _transactionViewMode

    var month: Month = Month.AUGUST
        set(value) {
            field = value
            _monthValue.postValue(value)
        }

    var year: Year = Year.now()
        set(value) {
            field = value
            _yearValue.postValue(value.value)
        }

    private val _monthValue: MutableLiveData<Month> = MutableLiveData()

    val monthValue: LiveData<Month>
        get() = _monthValue

    private val _yearValue: MutableLiveData<Int> = MutableLiveData()

    private val _scrollToDate: MutableLiveData<LocalDate> = MutableLiveData()

    val yearValue: LiveData<Int>
        get() = _yearValue

    val transactionItems: LiveData<List<TransactionItems>>
        get() = _transactionItems

    val scrollToDate: LiveData<LocalDate>
        get() = _scrollToDate

    val transactionItemsByWeek: LiveData<List<TransactionItemsByWeek>>
        get() = _transactionItemsByWeek

    val totalIncome: LiveData<Double>
        get() = _totalIncome
    val totalExpense: LiveData<Double>
        get() = _totalExpense

    fun getTransactionsViewMode() {
        _transactionViewMode.postValue(selectedTransactionsViewMode)
    }

    fun setTransactionsViewMode(transactionsViewMode: TransactionsViewMode) {
        selectedTransactionsViewMode = transactionsViewMode
        getTransactionsViewMode()
    }

    fun setScrollPosition(date: LocalDate) {
        setTransactionsViewMode(TransactionsViewMode.DAILY)
        _scrollToDate.postValue(date)
    }

    fun clearFilter(){ this.filterIDs.clear() }

    fun fetchTransactionsBetween(filterIDs: List<AccountID> = listOf()) {
        if (filterIDs.isNotEmpty()) clearFilter()
        this.filterIDs.addAll(filterIDs)

        val from: LocalDate = YearMonth.of(year.value, month).atEndOfMonth()
        val to: LocalDate = LocalDate.of(year.value, month, 1)

        viewModelScope.launch(Dispatchers.IO) {
            val data = transactionProvider.getTransactionsBetween(from, to) {
                if (this@TransactionProviderViewModel.filterIDs.isEmpty()) true
                else when (it) {
                    is FinancialTransaction -> this@TransactionProviderViewModel.filterIDs.contains(it.account.id)
                    is Transfer -> this@TransactionProviderViewModel.filterIDs.contains(it.fromAccount.id) || filterIDs.contains(it.toAccount.id)
                }
            }

            prepareTransactionItemsForDay(data)
            prepareTransactionItemsForWeek(data)
        }
    }

    fun fetchTransactionsMatches(query: String = "") {

        val from: LocalDate = YearMonth.of(year.value, month).atEndOfMonth()
        val to: LocalDate = LocalDate.of(year.value, month, 1)

        viewModelScope.launch {
            val data = transactionProvider.getTransactionsBetween(from, to) {
                if (query.isEmpty()) return@getTransactionsBetween false
                it.note.toString().lowercase().contains(query.lowercase())
            }

            prepareTransactionItemsForDay(data)
            prepareTransactionItemsForWeek(data)
        }
    }

    private fun prepareTransactionItemsForDay(transactionList: List<Transaction>) {
        val transactionItems: MutableList<TransactionItems> = mutableListOf()
        var totalIncome1 = 0.0
        var totalExpense1 = 0.0
        transactionList.sortedByDescending { it.date }.groupBy { it.date }
            .forEach { (date, values) ->
                val totalPeriodicIncome: Double =
                    values.filterIsInstance<Income>().sumOf { it.amount.toString().toDouble() }
                val totalPeriodicExpense: Double =
                    values.filterIsInstance<Expense>().sumOf { it.amount.toString().toDouble() }
                totalIncome1 += totalPeriodicIncome
                totalExpense1 += totalPeriodicExpense
                val periodicData = PeriodicDataByDay(
                    Helper.millisToDate(date),
                    totalPeriodicIncome.toString(),
                    totalPeriodicExpense.toString()
                )
                transactionItems.add(TransactionItems.PeriodicItem(periodicData))
                values.sortedByDescending { it.id }
                    .forEach { transactionItems.add(TransactionItems.TransactionItem(it)) }
            }

        Log.d(TAG, "prepareTransactionItemsForDay: $transactionItems")

        _transactionItems.postValue(transactionItems)
        _totalIncome.postValue(totalIncome1)
        _totalExpense.postValue(totalExpense1)
    }

    private fun prepareTransactionItemsForWeek(transactionList: List<Transaction>) {
        val transactionItems: MutableList<TransactionItemsByWeek> = mutableListOf()

        transactionList.sortedByDescending { it.date }
            .groupBy { Helper.millisToDate(it.date).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) }
            .forEach { (week, values) ->
                val groupsByDate: List<PeriodicDataByDay> =
                    values.groupBy { Helper.millisToDate(it.date) }.map { (date, trans) ->
                        val totalIncomeOnDate = trans.filterIsInstance<Income>()
                            .sumOf { it.amount.toString().toDouble() }
                        val totalExpenseOnDate = trans.filterIsInstance<Expense>()
                            .sumOf { it.amount.toString().toDouble() }
                        PeriodicDataByDay(
                            date,
                            totalIncomeOnDate.toString(),
                            totalExpenseOnDate.toString()
                        )
                    }.sortedByDescending { it.date }
                val totalPeriodicIncome: Double =
                    groupsByDate.sumOf { it.totalIncome.toDouble() }
                val totalPeriodicExpense: Double =
                    groupsByDate.sumOf { it.totalExpense.toDouble() }
                if (groupsByDate.isNotEmpty()) {
                    val startWeek = groupsByDate.last().date
                    val endWeek = groupsByDate.first().date
                    transactionItems.add(
                        TransactionItemsByWeek.PeriodicItem(
                            PeriodicDataByWeek(
                                startWeek,
                                endWeek,
                                week,
                                totalPeriodicIncome.toString(),
                                totalPeriodicExpense.toString()
                            )
                        )
                    )
                }
                transactionItems.addAll(groupsByDate.map { TransactionItemsByWeek.TransactionItem(it) })
            }

        Log.d(TAG, "prepareTransactionItemsForWeek: $transactionItems")

        _transactionItemsByWeek.postValue(transactionItems)
    }


}