package com.expensetracker.app.transactions.viewmodel

import android.app.Application
import android.util.Log
import android.widget.CheckBox
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.data.DataHandler
import com.expensetracker.app.transactions.adapter.TransactionsFilterAdapter
import com.expensetracker.app.transactions.support.TransactionFilterData
import com.expensetracker.core.models.AccountID
import com.expensetracker.core.models.BankAccount
import com.expensetracker.core.models.CashAccount
import com.expensetracker.core.models.CreditCard
import com.expensetracker.core.models.DebitCard
import com.expensetracker.core.models.Expense
import com.expensetracker.core.support.Literals.BANK_ACCOUNT
import com.expensetracker.core.support.Literals.CASH_ACCOUNT
import com.expensetracker.core.support.Literals.CREDIT_CARD
import com.expensetracker.core.support.Literals.DEBIT_CARD
import com.expensetracker.domain.contracts.account.AccountProvider
import com.expensetracker.domain.contracts.transaction.TransactionProvider
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.Year

class TransactionFilterViewModel(application: Application): AndroidViewModel(application) {
    
    init {
        Log.d(TAG, "init: ")
    }

    private val transactionProvider: TransactionProvider by lazy {
        DataHandler.transactionProvider
    }

    private val accountProvider: AccountProvider by lazy {
        DataHandler.accountProvider
    }

    private val _totalIncome: MutableLiveData<Double> = MutableLiveData()

    private val _totalExpense: MutableLiveData<Double> = MutableLiveData()

    private val _selectedIncome: MutableLiveData<Double> = MutableLiveData()

    private var selectedIncomeTracker: Double = 0.0

    private var selectedExpenseTracker: Double = 0.0

    private val _selectedExpense: MutableLiveData<Double> = MutableLiveData()

    private val _transactionsFilterAdapter: MutableLiveData<TransactionsFilterAdapter> = MutableLiveData()

    private val checkBoxes: MutableMap<AccountID,CheckBox> = mutableMapOf()

    private val _selectedAccounts: MutableLiveData<List<Int>> = MutableLiveData()

    val totalIncome: LiveData<Double>
        get() = _totalIncome

    val totalExpense: LiveData<Double>
        get() = _totalExpense

    val selectedIncome: LiveData<Double>
        get() = _selectedIncome

    val selectedExpense: LiveData<Double>
        get() = _selectedExpense

    val selectedAccounts: LiveData<List<Int>>
        get() = _selectedAccounts

    val transactionsFilterAdapter: LiveData<TransactionsFilterAdapter>
        get() = _transactionsFilterAdapter

    fun prepareTransactionFilter(year: Year, month: Month, selectedAccounts: List<AccountID>) {
        viewModelScope.launch {
            val incomeTransactions = transactionProvider.getIncomeBetween(
                from = LocalDate.of(year.value, month, 1),
                to = LocalDate.of(year.value, month, month.length(year.isLeap))
            )
            val expenseTransactions = transactionProvider.getExpenseBetween(
                from = LocalDate.of(year.value, month, 1),
                to = LocalDate.of(year.value, month, month.length(year.isLeap))
            )
            val transferTransactions = transactionProvider.getTransferBetween(
                from = LocalDate.of(year.value, month, 1),
                to = LocalDate.of(year.value, month, month.length(year.isLeap))
            )
            val accounts = accountProvider.accounts

            val accountIncomes: Map<Int, Double> =
                incomeTransactions.groupBy { it.account.id }.mapKeys { it.key }.mapValues { incomes ->
                    incomes.value.sumOf { it.amount.toString().toDouble() }
                }
            val accountExpenses: Map<Int, Double> =
                expenseTransactions.groupBy { it.account.id }.mapKeys { it.key }.mapValues { expenses ->
                    expenses.value.sumOf { it.amount.toString().toDouble() }
                }
            val accountTransferIns: Map<Int, Double> =
                transferTransactions.groupBy { it.toAccount.id }.mapKeys { it.key }.mapValues { transfers ->
                    transfers.value.sumOf { it.amount.toString().toDouble() }
                }
            val accountTransferOuts: Map<Int, Double> =
                transferTransactions.groupBy { it.fromAccount.id }.mapKeys { it.key }.mapValues { transfers ->
                    transfers.value.sumOf { it.amount.toString().toDouble() }
                }

            val transactionFilterDataList: MutableList<TransactionFilterData> = mutableListOf()

            val bankAccountsData: MutableList<TransactionFilterData.TransactionFilterItem> =
                mutableListOf()
            val cashAccountsData: MutableList<TransactionFilterData.TransactionFilterItem> =
                mutableListOf()
            val creditCardsData: MutableList<TransactionFilterData.TransactionFilterItem> =
                mutableListOf()
            val debitCardsData: MutableList<TransactionFilterData.TransactionFilterItem> =
                mutableListOf()

            accounts.forEach { account ->
                val income: Double = accountIncomes[account.id] ?: 0.0
                val expense: Double = accountExpenses[account.id] ?: 0.0
                val transferIn: Double = accountTransferIns[account.id] ?: 0.0
                val transferOut: Double = accountTransferOuts[account.id] ?: 0.0
                when (account) {
                is CreditCard -> creditCardsData
                is DebitCard -> debitCardsData
                is BankAccount -> bankAccountsData
                is CashAccount -> cashAccountsData
            }.add(
                    TransactionFilterData.TransactionFilterItem(
                        account.id,
                        account.name.toString(),
                        income,
                        expense,
                        transferIn,
                        transferOut
                    )
                )
            }

            if (bankAccountsData.isNotEmpty()) {
                transactionFilterDataList.also {
                    it.add(TransactionFilterData.TransactionFilterTitle(BANK_ACCOUNT))
                    it.addAll(bankAccountsData)
                }
            }
            if (cashAccountsData.isNotEmpty()) {
                transactionFilterDataList.also {
                    it.add(TransactionFilterData.TransactionFilterTitle(CASH_ACCOUNT))
                    it.addAll(cashAccountsData)
                }
            }
            if (creditCardsData.isNotEmpty()) {
                transactionFilterDataList.also {
                    it.add(TransactionFilterData.TransactionFilterTitle(CREDIT_CARD))
                    it.addAll(creditCardsData)
                }
            }
            if (debitCardsData.isNotEmpty()) {
                transactionFilterDataList.also {
                    it.add(TransactionFilterData.TransactionFilterTitle(DEBIT_CARD))
                    it.addAll(debitCardsData)
                }
            }

            val selected: Set<AccountID> = mutableSetOf<AccountID>().also { set ->
                if(checkBoxes.isNotEmpty()) set.addAll(checkBoxes.filter { it.value.isChecked }.keys)
                set.addAll(selectedAccounts)
            }

            _transactionsFilterAdapter.postValue(
                TransactionsFilterAdapter(
                    transactionFilterDataList,
                    selected,
                    checkBoxes,
                    onFilterApplied = { data ->
                        Log.d(TAG, "onFilterApplied $data ")
                        updateTotals(data)
                    },
                    onFilterRemoved = { data ->
                        Log.d(TAG, "onFilterRemoved ${checkBoxes[data.accountID]?.isChecked}")
                        updateTotals(data)
                    })
            )

            accountIncomes.values.sumOf { it }.let { _totalIncome.postValue(it) }
            accountExpenses.values.sumOf { it }.let { _totalExpense.postValue(it) }
        }
    }

    fun selectAccounts(accountIDs: List<AccountID>) {
        if(checkBoxes.isNotEmpty()){
            accountIDs.forEach { 
                if(checkBoxes.contains(it)){
                    checkBoxes[it]?.isChecked = true
                }
            }
        }
    }

    fun updateTotals(data:  TransactionFilterData. TransactionFilterItem){
        if(checkBoxes.containsKey(data.accountID) && checkBoxes[data.accountID]?.isChecked == true){
            selectedIncomeTracker += data.income
            selectedExpenseTracker += data.expense
        } else {
            selectedIncomeTracker -= data.income
            selectedExpenseTracker -= data.expense
        }
        _selectedAccounts.postValue(selectedAccountIds())
        _selectedIncome.postValue(selectedIncomeTracker)
        _selectedExpense.postValue(selectedExpenseTracker)
    }
    
    fun selectedAccountIds(): List<Int> {
        return checkBoxes.filter { it.value.isChecked }.keys.toList()
    }
}