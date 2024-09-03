package com.expensetracker.app.accounts.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.accounts.support.AccountListData
import com.expensetracker.app.data.DataHandler
import com.expensetracker.core.models.Account
import com.expensetracker.core.models.AccountID
import com.expensetracker.core.models.BankAccount
import com.expensetracker.core.models.CashAccount
import com.expensetracker.core.models.CreditCard
import com.expensetracker.core.models.DebitCard
import com.expensetracker.core.support.AccountResponse
import com.expensetracker.core.support.AccountType
import com.expensetracker.core.support.Amount
import com.expensetracker.core.support.Literals.BANK_ACCOUNT
import com.expensetracker.core.support.Literals.CASH_ACCOUNT
import com.expensetracker.core.support.Literals.CREDIT_CARD
import com.expensetracker.core.support.Literals.DEBIT_CARD
import com.expensetracker.domain.contracts.account.AccountManager
import com.expensetracker.domain.support.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountsViewModel(application: Application) : AndroidViewModel(application) {

    private val accountManager: AccountManager by lazy {
        DataHandler(application).accountManager
    }

    private val _total: MutableLiveData<Double> = MutableLiveData()

    private val _liabilities: MutableLiveData<Double> = MutableLiveData()

    private val _isRemovable: MutableLiveData<Boolean> = MutableLiveData()

    private var canRemove: Boolean = false

    private val _bankAccounts: MutableLiveData<List<BankAccount>> = MutableLiveData()

    private val _accountListData: MutableLiveData<List<AccountListData>> = MutableLiveData()

    private val _updateAccount: MutableLiveData<Account?> = MutableLiveData()

    val total: LiveData<Double>
        get() = _total

    val liabilities: LiveData<Double>
        get() = _liabilities

    val isRemovable: LiveData<Boolean>
        get() = _isRemovable

    val bankAccounts: LiveData<List<BankAccount>>
        get() = _bankAccounts

    val accountListData: LiveData<List<AccountListData>>
        get() = _accountListData

    val updateAccount: LiveData<Account?>
        get() = _updateAccount

    fun setRemovable(bool: Boolean) {
        canRemove = bool
        _isRemovable.postValue(canRemove)
        fetchAccounts()
    }

    fun getIsRemovable() {
        _isRemovable.postValue(canRemove)
    }

    fun fetchAccounts() {
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

            _accountListData.postValue(accountListData)
            _total.postValue(total1)
            _liabilities.postValue(liabilities1)
        }
    }

    fun getBankAccounts() {
        viewModelScope.launch {
            _bankAccounts.postValue(accountManager.bankAccounts)
        }
    }

    fun createAccount(accountType: AccountType, name: String, bankAccount: BankAccount? = null) {
        viewModelScope.launch {
            when (accountType) {
                AccountType.BANK_ACCOUNT -> accountManager.createBankAccount(name)
                AccountType.CASH_ACCOUNT -> accountManager.createCashAccount(name)
                AccountType.CREDIT_CARD -> accountManager.createCreditCard(name)
                AccountType.DEBIT_CARD -> {
                    if (bankAccount == null) {
                        Result.Failure("No BankAccount Found")
                    } else
                        accountManager.createDebitCard(name, bankAccount)
                }
            }.let {
                when (it) {
                    is Result.Failure -> Toast.makeText(getApplication(), "$it", Toast.LENGTH_SHORT)
                        .show()

                    is Result.Success -> Toast.makeText(
                        getApplication(),
                        "Account Created",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun updateAccountName(account: Account,  name: String){
        viewModelScope.launch(Dispatchers.IO) {
            when(account){
                is CreditCard -> accountManager.updateCreditCard(account,name)
                is DebitCard -> accountManager.updateDebitCard(account,name)
                is BankAccount -> accountManager.updateBankAccount(account,name)
                is CashAccount -> accountManager.updateCashAccount(account,name)
            }.let { result ->
                when(result){
                    is Result.Failure -> result.data
                    is Result.Success -> "Account Updated"
                }.let {
                    Toast.makeText(getApplication(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun updateBankAccount(bankAccount: BankAccount, newAccountType: AccountType, name: String,  selectedBankAccount: BankAccount? = null){
        when(newAccountType){
            AccountType.BANK_ACCOUNT -> accountManager.updateBankAccount(bankAccount,name)
            AccountType.DEBIT_CARD -> {
                if(selectedBankAccount == null){
                    Result.Failure(AccountResponse.ACCOUNT_NOT_EXIST.toString())
                } else {
                    val result = accountManager.createDebitCard(name, selectedBankAccount)
                    if(result is Result.Success)
                        accountManager.deleteAccount(bankAccount)
                    else result
                }
            }
            AccountType.CREDIT_CARD -> {
                val result = accountManager.createCreditCard(name, balance = bankAccount.balance.toString(), outStandings = Amount.DEFAULT.toString())
                if(result is Result.Success)
                    accountManager.deleteAccount(bankAccount)
                else result
            }
            AccountType.CASH_ACCOUNT -> {
                val result = accountManager.createCashAccount(name, balance = bankAccount.balance.toString(), minimumBalance = bankAccount.minimumBalance.toString())
                if(result is Result.Success)
                    accountManager.deleteAccount(bankAccount)
                else result
            }
        }.let {
            result ->
            Toast.makeText(getApplication(), result.data, Toast.LENGTH_SHORT).show()
        }
    }

    fun updateCashAccount(cashAccount: CashAccount, newAccountType: AccountType, name: String,  selectedBankAccount: BankAccount? = null){
        when(newAccountType){
            AccountType.CASH_ACCOUNT -> accountManager.updateCashAccount(cashAccount,name)
            AccountType.DEBIT_CARD -> {
                if(selectedBankAccount == null){
                    Result.Failure(AccountResponse.ACCOUNT_NOT_EXIST.toString())
                } else {
                    val result = accountManager.createDebitCard(name, selectedBankAccount)
                    if(result is Result.Success)
                        accountManager.deleteAccount(cashAccount)
                    else result
                }
            }
            AccountType.CREDIT_CARD -> {
                val result = accountManager.createCreditCard(name, balance = cashAccount.balance.toString(), outStandings = Amount.DEFAULT.toString())
                if(result is Result.Success)
                    accountManager.deleteAccount(cashAccount)
                else result
            }
            AccountType.BANK_ACCOUNT -> {
                val result = accountManager.createBankAccount(name, balance = cashAccount.balance.toString(), minimumBalance = cashAccount.minimumBalance.toString())
                if(result is Result.Success)
                    accountManager.deleteAccount(cashAccount)
                else result
            }
        }.let {
                result ->
            Toast.makeText(getApplication(), result.data, Toast.LENGTH_SHORT).show()
        }
    }

    fun updateCreditCard(account: CreditCard, newAccountType: AccountType, name: String, selectedBankAccount: BankAccount? = null){
        when(newAccountType){
            AccountType.CREDIT_CARD -> accountManager.updateCreditCard(account,name)
            AccountType.DEBIT_CARD -> {
                if(selectedBankAccount == null){
                    Result.Failure(AccountResponse.ACCOUNT_NOT_EXIST.toString())
                } else {
                    val result = accountManager.createDebitCard(name, selectedBankAccount)
                    if(result is Result.Success)
                        accountManager.deleteAccount(account)
                    else result
                }
            }
            AccountType.CASH_ACCOUNT -> {
                val result = accountManager.createCashAccount(name, balance = Amount.DEFAULT.toString())
                if(result is Result.Success)
                    accountManager.deleteAccount(account)
                else result
            }
            AccountType.BANK_ACCOUNT -> {
                val result = accountManager.createBankAccount(name, balance = Amount.DEFAULT.toString())
                if(result is Result.Success)
                    accountManager.deleteAccount(account)
                else result
            }
        }.let {
                result ->
            Toast.makeText(getApplication(), result.data, Toast.LENGTH_SHORT).show()
        }
    }

    fun updateDebitCard(account: DebitCard, newAccountType: AccountType, name: String, selectedBankAccount: BankAccount? = null){
        when(newAccountType){
            AccountType.DEBIT_CARD -> accountManager.updateDebitCard(account,name)
            AccountType.CREDIT_CARD -> {
                val result = accountManager.createCreditCard(name)
                if(result is Result.Success)
                    accountManager.deleteAccount(account)
                else result
            }
            AccountType.CASH_ACCOUNT -> {
                val result = accountManager.createCashAccount(name, balance = account.balance.toString())
                if(result is Result.Success)
                    accountManager.deleteAccount(account)
                else result
            }
            AccountType.BANK_ACCOUNT -> {
                val result = accountManager.createBankAccount(name, balance = account.balance.toString())
                if(result is Result.Success)
                    accountManager.deleteAccount(account)
                else result
            }
        }.let {
                result ->
            Toast.makeText(getApplication(), result.data, Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteAccount(account: Account) {
        accountManager.deleteAccount(account).let { result ->
            when (result) {
                is Result.Failure -> result.data
                is Result.Success -> "Account Deleted"
            }.let {
                Toast.makeText(getApplication(), it, Toast.LENGTH_SHORT).show()
                fetchAccounts()
            }
        }
    }

    fun getAccount(accountID: AccountID) {
        viewModelScope.launch(Dispatchers.IO) {
            _updateAccount.postValue(accountManager.accounts.getOrNull(accountID))
        }
    }

}