package com.expensetracker.app.transactions.viewmodel

import android.app.Application
import android.media.audiofx.HapticGenerator
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.data.DataHandler
import com.expensetracker.app.transactions.support.PeriodicDataByDay
import com.expensetracker.app.transactions.support.PeriodicDataByMonth
import com.expensetracker.app.transactions.support.PeriodicDataByWeek
import com.expensetracker.app.transactions.support.SearchMode
import com.expensetracker.app.transactions.support.TransactionItems
import com.expensetracker.app.transactions.support.TransactionItemsByMonth
import com.expensetracker.app.transactions.support.TransactionItemsByWeek
import com.expensetracker.app.transactions.support.TransactionsDisplayMode
import com.expensetracker.app.transactions.support.WeekNumber
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth
import java.time.temporal.IsoFields
import java.time.temporal.TemporalAdjusters
import kotlin.time.measureTime

const val TAG = "TransactionsViewModel=>log"

class TransactionProviderViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionProvider: TransactionProvider by lazy {
        DataHandler(application).transactionProvider
    }

    private val _totalIncome: MutableLiveData<Double> = MutableLiveData(0.0)
    private val _totalExpense: MutableLiveData<Double> = MutableLiveData(0.0)

    var selectedTransactionsDisplayMode: TransactionsDisplayMode = TransactionsDisplayMode.DAILY
        private set

    var searchMode: SearchMode = SearchMode.NOTE
        set(value) {
            field = value
            fetchTransactionsMatches()
        }

    private val _transactionViewMode: MutableLiveData<TransactionsDisplayMode> = MutableLiveData()

    private val _transactionItems: MutableLiveData<List<TransactionItems>> = MutableLiveData()

    private val _transactionItemsByWeek: MutableLiveData<List<TransactionItemsByWeek>> =
        MutableLiveData()

    private val _transactionItemsByMonth: MutableLiveData<List<TransactionItemsByMonth>> =
        MutableLiveData()

    private val filterIDs: MutableSet<Int> = mutableSetOf()

    val transactionsDisplayMode: LiveData<TransactionsDisplayMode>
        get() = _transactionViewMode

    var month: Month = LocalDate.now().month
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

    private val _scrollToWeek: MutableLiveData<WeekNumber> = MutableLiveData()

    private val _scrollToView: MutableLiveData<Int> = MutableLiveData()

    val yearValue: LiveData<Int>
        get() = _yearValue

    val transactionItems: LiveData<List<TransactionItems>>
        get() = _transactionItems

    val scrollToDate: LiveData<LocalDate>
        get() = _scrollToDate

    val scrollToWeek: LiveData<WeekNumber>
            get() = _scrollToWeek

    val scrollToView: LiveData<Int>
        get() = _scrollToView

    val transactionItemsByWeek: LiveData<List<TransactionItemsByWeek>>
        get() = _transactionItemsByWeek

    val transactionItemsByMonth: LiveData<List<TransactionItemsByMonth>>
        get() = _transactionItemsByMonth

    val totalIncome: LiveData<Double>
        get() = _totalIncome
    val totalExpense: LiveData<Double>
        get() = _totalExpense

    fun getTransactionsViewMode() {
        _transactionViewMode.postValue(selectedTransactionsDisplayMode)
    }


    fun setTransactionsViewMode(transactionsDisplayMode: TransactionsDisplayMode) {
        selectedTransactionsDisplayMode = transactionsDisplayMode
        getTransactionsViewMode()
    }

    fun setScrollPosition(position: Int) {
        _scrollToView.postValue(position)
    }

    fun setScrollPosition(weekNumber: WeekNumber, month: Month) {
        setTransactionsViewMode(TransactionsDisplayMode.WEEKLY)
        this.month = month
        _scrollToWeek.postValue(weekNumber)
    }
    fun setScrollPosition(date: LocalDate) {
        setTransactionsViewMode(TransactionsDisplayMode.DAILY)
        _scrollToDate.postValue(date)
    }

    fun clearFilter(){ this.filterIDs.clear() }

    fun fetchTransactionsBetween(filterIDs: List<AccountID> = listOf()) {
        if (filterIDs.isNotEmpty()) clearFilter()
        this.filterIDs.addAll(filterIDs)

        val from: LocalDate = YearMonth.of(year.value, month).atEndOfMonth()
        val to: LocalDate = LocalDate.of(year.value, month, 1)

        viewModelScope.launch(Dispatchers.IO) {
            measureTime {
                val data = transactionProvider.getTransactionsBetween(from, to) {
                    if (this@TransactionProviderViewModel.filterIDs.isEmpty()) true
                    else when (it) {
                        is FinancialTransaction -> this@TransactionProviderViewModel.filterIDs.contains(
                            it.account.id
                        )

                        is Transfer -> this@TransactionProviderViewModel.filterIDs.contains(it.fromAccount.id) || filterIDs.contains(
                            it.toAccount.id
                        )
                    }
                }

                prepareTransactionItemsByDay(data)
                prepareTransactionItemsByWeek(data)
            }.let { Log.d(TAG, "fetchTransactionsBetween: TIME 1 => $it") }
        }
    }

    fun fetchTransactionsMatches(query: String = "") {

        val from: LocalDate = YearMonth.of(year.value, month).atEndOfMonth()
        val to: LocalDate = LocalDate.of(year.value, month, 1)

        viewModelScope.launch {
            val data = transactionProvider.getTransactionsBetween(from, to) {
                Log.d(TAG, "fetchTransactionsMatches: $searchMode $query")
                if (query.isEmpty()) return@getTransactionsBetween false
                when(searchMode){
                    SearchMode.NOTE -> it.note.toString().lowercase().contains(query.lowercase())
                    SearchMode.ACCOUNT -> {
                        when(it){
                            is FinancialTransaction -> it.account.name.toString().lowercase().contains(query.lowercase())
                            is Transfer -> it.fromAccount.name.toString().lowercase().contains(query.lowercase()) ||
                                    it.toAccount.name.toString().lowercase().contains(query.lowercase())
                        }
                    }
                    SearchMode.CATEGORY -> {
                        when(it){
                            is FinancialTransaction -> it.category.name.toString().lowercase().contains(query.lowercase())
                            else -> false
                        }
                    }
                    SearchMode.AMOUNT -> {
                        it.amount.toString().lowercase().contains(query.lowercase())
                    }
                }
            }

            prepareTransactionItemsByDay(data)
        }
    }

    private fun prepareTransactionItemsByDay(transactionList: List<Transaction>) {
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

    private fun prepareTransactionItemsByWeek(transactionList: List<Transaction>) {
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

    fun prepareTransactionItemsByMonth(year: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val t = measureTime {
                val transactionItems: MutableList<TransactionItemsByMonth> = mutableListOf()
                try {
                    Month.entries.forEach { month ->
                        var totalIncome: Double = 0.0
                        var totalExpense: Double = 0.0
                        val weeksList: MutableList<TransactionItemsByMonth.TransactionItem> =
                            mutableListOf()

                        getWeeks(year, month.value).forEach {
                            val start = it.first
                            val end = it.second
                            val weekNumber: WeekNumber =
                                start.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
                            val income: Double =
                                transactionProvider.getTotalOfIncomeBetween(end, start).toDouble()
                            val expense: Double =
                                transactionProvider.getTotalOfExpenseBetween(end, start).toDouble()
                            totalIncome += income
                            totalExpense += expense
                            weeksList.add(
                                TransactionItemsByMonth.TransactionItem(
                                    PeriodicDataByWeek(
                                        start,
                                        end,
                                        weekNumber,
                                        income.toString(),
                                        expense.toString()
                                    )
                                )
                            )
                        }

                        transactionItems.add(
                            TransactionItemsByMonth.PeriodicItem(
                                PeriodicDataByMonth(
                                    month,
                                    Year.of(year),
                                    totalIncome.toString(),
                                    totalExpense.toString()
                                )
                            )
                        )
                        transactionItems.addAll(weeksList)
                    }
                _transactionItemsByMonth.postValue(transactionItems)
                } catch (_: Exception) {
                    _transactionItemsByMonth.postValue(mutableListOf())
                }
            }
            Log.d(TAG, "prepareTransactionItemsByMonth: TIME => $t")
        }

    }

    private fun getWeeks(year:Int, month:Int): List<Pair<LocalDate, LocalDate>> {
        val firstDayOfMonth = LocalDate.of(year, month, 1)
        val lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth())

        val weeks = mutableListOf<Pair<LocalDate, LocalDate>>()

        var currentWeekStart = firstDayOfMonth
        while (currentWeekStart.isBefore(lastDayOfMonth) || currentWeekStart == lastDayOfMonth) {

            val weekStart = if (currentWeekStart.dayOfWeek != DayOfWeek.MONDAY) {
                currentWeekStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            } else {
                currentWeekStart
            }

            var weekEnd = currentWeekStart.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

            if (weekEnd > lastDayOfMonth) weekEnd = lastDayOfMonth

            if (weekStart.isBefore(firstDayOfMonth)) {
                weeks.add(firstDayOfMonth to weekEnd)
            } else {
                weeks.add(weekStart to weekEnd)
            }

            currentWeekStart = weekEnd.plusDays(1)
        }
        return weeks
    }


}