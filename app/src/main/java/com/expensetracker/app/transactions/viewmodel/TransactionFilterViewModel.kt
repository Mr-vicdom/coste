package com.expensetracker.app.transactions.viewmodel

import android.app.Application
import android.util.Log
import android.widget.CheckBox
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.data.DataHandler
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

    private val dataHandler = DataHandler(application)

    private val transactionProvider: TransactionProvider by lazy {
        dataHandler.transactionProvider
    }

    private val accountProvider: AccountProvider by lazy {
        dataHandler.accountProvider
    }

    private val _totalIncome: MutableLiveData<Double> = MutableLiveData()

    private val _totalExpense: MutableLiveData<Double> = MutableLiveData()

    private val _selectedIncome: MutableLiveData<Double> = MutableLiveData()

    private var selectedIncomeTracker: Double = 0.0

    private var selectedExpenseTracker: Double = 0.0

    private val _selectedExpense: MutableLiveData<Double> = MutableLiveData()

    private val _selectedAccounts: MutableLiveData<List<Int>> = MutableLiveData()

    private val selectedAccountsList: MutableList<Int> = mutableListOf()

    private val _transactionFilterData: MutableLiveData<List<TransactionFilterData>> = MutableLiveData()

    val transactionFilterData: LiveData<List<TransactionFilterData>>
        get() = _transactionFilterData

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

    fun prepareTransactionFilter(year: Year, month: Month, selectedAccountIds: Set<AccountID> ) {
        viewModelScope.launch {

            val selectedAccounts: MutableSet<AccountID> = mutableSetOf()
            if(selectedAccountIds.isNotEmpty()) {
                selectedAccounts.addAll(selectedAccountIds)
                selectedAccountsList.addAll(selectedAccountIds)
            }
            if(selectedAccountsList.isNotEmpty()) selectedAccounts.addAll(selectedAccountsList)

            Log.d(TAG, "prepareTransactionFilter: $selectedAccountIds")

            selectedExpenseTracker = 0.0
            selectedIncomeTracker = 0.0

            val incomeTransactions = transactionProvider.getIncomeBetween(
                from = LocalDate.of(year.value, month, month.length(year.isLeap)),
                to = LocalDate.of(year.value, month, 1)
            )
            val expenseTransactions = transactionProvider.getExpenseBetween(
                from = LocalDate.of(year.value, month, month.length(year.isLeap)),
                to = LocalDate.of(year.value, month, 1),
            )
            val transferTransactions = transactionProvider.getTransferBetween(
                from = LocalDate.of(year.value, month, month.length(year.isLeap)),
                to = LocalDate.of(year.value, month, 1),
            )
            val accounts = accountProvider.accounts

            val accountIncomes: Map<Int, Double> =
                incomeTransactions.groupBy { it.account.id }.mapValues { incomes ->
                    if (incomes.key in selectedAccounts) selectedIncomeTracker += incomes.value.sumOf { it.amount.toString().toDouble() }
                    incomes.value.sumOf { it.amount.toString().toDouble() }
                }
            val accountExpenses: Map<Int, Double> =
                expenseTransactions.groupBy { it.account.id }.mapValues { expenses ->
                    if (expenses.key in selectedAccounts) selectedExpenseTracker += expenses.value.sumOf { it.amount.toString().toDouble() }
                    expenses.value.sumOf { it.amount.toString().toDouble() }
                }
            val accountTransferIns: Map<Int, Double> =
                transferTransactions.groupBy { it.toAccount.id }.mapValues { transfers ->
                    transfers.value.sumOf { it.amount.toString().toDouble() }
                }
            val accountTransferOuts: Map<Int, Double> =
                transferTransactions.groupBy { it.fromAccount.id }.mapValues { transfers ->
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

            _transactionFilterData.postValue(transactionFilterDataList)

            accountIncomes.values.sumOf { it }.let { _totalIncome.postValue(it) }
            accountExpenses.values.sumOf { it }.let { _totalExpense.postValue(it) }
            if (selectedAccounts.isNotEmpty()){
                _selectedIncome.postValue(selectedIncomeTracker)
                _selectedExpense.postValue(selectedExpenseTracker)
            }
        }
    }

    fun getSelectedIds() {
        viewModelScope.launch {
            _selectedAccounts.postValue(selectedAccountsList)
        }
    }


    fun updateTotals(data: TransactionFilterData.TransactionFilterItem){
        if(!selectedAccountsList.contains(data.accountID)){
            selectedAccountsList.add(data.accountID)
            selectedIncomeTracker += data.income
            selectedExpenseTracker += data.expense
        } else {
            Log.d(TAG, "updateTotals: removed")
            selectedAccountsList.remove(data.accountID)
            selectedIncomeTracker -= data.income
            selectedExpenseTracker -= data.expense
        }
        _selectedIncome.postValue(selectedIncomeTracker)
        _selectedExpense.postValue(selectedExpenseTracker)
        _selectedAccounts.postValue(selectedAccountsList)
    }

}