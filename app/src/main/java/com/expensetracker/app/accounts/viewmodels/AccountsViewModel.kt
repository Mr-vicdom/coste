package com.expensetracker.app.accounts.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.accounts.adapter.AccountsListAdapter
import com.expensetracker.app.accounts.support.AccountListData
import com.expensetracker.app.data.DataHandler
import com.expensetracker.core.models.Account
import com.expensetracker.core.models.BankAccount
import com.expensetracker.core.support.AccountType
import com.expensetracker.core.support.Literals.BANK_ACCOUNT
import com.expensetracker.core.support.Literals.CASH_ACCOUNT
import com.expensetracker.core.support.Literals.CREDIT_CARD
import com.expensetracker.core.support.Literals.DEBIT_CARD
import com.expensetracker.domain.contracts.account.AccountManager
import com.expensetracker.domain.contracts.account.AccountProvider
import com.expensetracker.domain.support.Result
import kotlinx.coroutines.launch

class AccountsViewModel(application: Application): AndroidViewModel(application) {

    private val accountManager: AccountManager by lazy {
        DataHandler(application).accountManager
    }

    private val _listAdapter: MutableLiveData<AccountsListAdapter> = MutableLiveData()

    private val _total: MutableLiveData<Double> = MutableLiveData()

    private val _liabilities: MutableLiveData<Double> = MutableLiveData()

    private val _isRemovable: MutableLiveData<Boolean> = MutableLiveData()

    private var canRemove: Boolean = false

    private val _bankAccounts: MutableLiveData<List<BankAccount>> = MutableLiveData()

    val listAdapter: LiveData<AccountsListAdapter>
        get() = _listAdapter

    val total: LiveData<Double>
        get() = _total

    val liabilities: LiveData<Double>
        get() = _liabilities

    val isRemovable: LiveData<Boolean>
        get() = _isRemovable

    val bankAccounts: LiveData<List<BankAccount>>
        get() = _bankAccounts

    fun setRemovable(bool: Boolean){
        canRemove = bool
        _isRemovable.postValue(canRemove)
        getAdapter()
    }

    fun getIsRemovable(){
        _isRemovable.postValue(canRemove)
    }

    fun getAdapter() {
        viewModelScope.launch {
            val bankAccounts = accountManager.bankAccounts
            val cashAccounts = accountManager.cashAccounts
            val creditCards = accountManager.creditCards
            val debitCards = accountManager.debitCards

            val accountListData: MutableList<AccountListData> = mutableListOf()

            accountListData.add(AccountListData.AccountTitle(BANK_ACCOUNT))
            accountListData.addAll(bankAccounts.map { AccountListData.AccountItem(it) })

            accountListData.add(AccountListData.AccountTitle(CASH_ACCOUNT))
            accountListData.addAll(cashAccounts.map { AccountListData.AccountItem(it) })

            accountListData.add(AccountListData.AccountTitle(CREDIT_CARD))
            accountListData.addAll(creditCards.map { AccountListData.AccountItem(it) })

            accountListData.add(AccountListData.AccountTitle(DEBIT_CARD))
            accountListData.addAll(debitCards.map { AccountListData.AccountItem(it) })

            var total1: Double = 0.0
            total1 += bankAccounts.sumOf { it.balance.toString().toDouble() }
            total1 += cashAccounts.sumOf { it.balance.toString().toDouble() }

            var liabilities1: Double = 0.0
            liabilities1 += creditCards.sumOf {
                Log.d("=>log", "getAdapter: ${it.outStandings}")
                it.outStandings.toString().toDouble()
            }

            Log.d("=>log", "getAdapter: $liabilities1")

            val accountsListAdapter = if(canRemove){
                AccountsListAdapter(accountListData,true, onAccountRemoved = {
                    accountManager.deleteAccount(it)
                    getAdapter()
                })
            } else {
                AccountsListAdapter(accountListData,false){

                }
            }

            _listAdapter.postValue(accountsListAdapter)
            _total.postValue(total1)
            _liabilities.postValue(liabilities1)
        }
    }

    fun getBankAccounts() {
        viewModelScope.launch {
            _bankAccounts.postValue(accountManager.bankAccounts)
        }
    }

    fun createAccount(accountType: AccountType,name: String, bankAccount: BankAccount? = null) {
        viewModelScope.launch {
            when(accountType){
                AccountType.BANK_ACCOUNT -> accountManager.createBankAccount(name)
                AccountType.CASH_ACCOUNT -> accountManager.createCashAccount(name)
                AccountType.CREDIT_CARD -> accountManager.createCreditCard(name)
                AccountType.DEBIT_CARD -> {
                    if(bankAccount == null){
                        Result.Failure("No BankAccount Found")
                    } else
                    accountManager.createDebitCard(name,bankAccount)
                }
            }.let {
                when(it){
                    is Result.Failure -> Toast.makeText(getApplication(), "$it", Toast.LENGTH_SHORT).show()
                    is Result.Success -> Toast.makeText(getApplication(), "Account Created", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}