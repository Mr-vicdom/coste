package com.expensetracker.app.transactions.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.data.DataHandler
import com.expensetracker.app.data.DataHandler.accountManager
import com.expensetracker.app.data.DataHandler.categoryManager
import com.expensetracker.app.data.DataHandler.incomeActions
import com.expensetracker.app.data.DataHandler.transactionProvider
import com.expensetracker.app.support.DataGenerator
import com.expensetracker.core.models.Account
import com.expensetracker.core.models.AccountID
import com.expensetracker.core.models.Category
import com.expensetracker.core.models.CategoryID
import com.expensetracker.core.models.Expense
import com.expensetracker.core.models.Income
import com.expensetracker.core.models.Transaction
import com.expensetracker.core.models.TransactionID
import com.expensetracker.core.models.Transfer
import com.expensetracker.core.support.TransactionType
import com.expensetracker.domain.contracts.account.AccountProvider
import com.expensetracker.domain.contracts.category.CategoryProvider
import com.expensetracker.domain.contracts.transaction.TransactionManager
import com.expensetracker.domain.support.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class TransactionManagerViewModel(private val application: Application): AndroidViewModel(application) {

    private val transactionManager: TransactionManager by lazy {
        DataHandler.transactionManager
    }

    private val categoryProvider: CategoryProvider by lazy {
        DataHandler.categoryProvider
    }

    private val accountProvider: AccountProvider by lazy {
        DataHandler.accountProvider
    }

    private val _incomeCategories: MutableLiveData<Map<CategoryID,String>> = MutableLiveData(
        emptyMap()
    )

    private val _expenseCategories: MutableLiveData<Map<CategoryID,String>> = MutableLiveData(
        emptyMap()
    )

    private val _accounts: MutableLiveData<Map<AccountID,String>> = MutableLiveData(emptyMap())

    private val _isLoaded: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _transactionType: MutableLiveData<TransactionType> = MutableLiveData(TransactionType.EXPENSE)

    private val _existingTransaction: MutableLiveData<Transaction?> = MutableLiveData()


    val incomeCategories: LiveData<Map<CategoryID,String>>
        get() = _incomeCategories

    val expenseCategories: LiveData<Map<CategoryID,String>>
        get() = _expenseCategories

    val accounts: LiveData<Map<AccountID,String>>
        get() = _accounts

    val isLoaded: LiveData<Boolean>
        get() = _isLoaded

    val transactionType: LiveData<TransactionType>
        get() = _transactionType

    val existingTransaction: LiveData<Transaction?>
        get() = _existingTransaction

    fun fetchData(){
        viewModelScope.launch {
            _incomeCategories.postValue(categoryProvider.incomeCategories.associate { it.id to it.name.toString() })
            _expenseCategories.postValue(categoryProvider.expenseCategories.associate { it.id to it.name.toString() })
            _accounts.postValue(accountProvider.accounts.associate { it.id to it.name.toString() })
            _isLoaded.postValue(true)
        }
    }

    fun setTransactionType(transactionType: TransactionType){
        _transactionType.postValue(transactionType)
    }

    fun addIncome(date: LocalDate, amount: String, note: String, description: String = "", categoryID: CategoryID, accountID: AccountID) {

        viewModelScope.launch {
            val category: Category? = categoryProvider.incomeCategories.find { it.id == categoryID }
            val account: Account? = accountProvider.accounts.find { it.id == accountID }

            if(category == null) {
                Toast.makeText(application,"Invalid Income Category $categoryID",Toast.LENGTH_SHORT).show()
                return@launch
            } else if(account == null) {
                Toast.makeText(application,"Invalid Account",Toast.LENGTH_SHORT).show()
                return@launch
            }

            val result: Result = transactionManager.addIncome(date,amount,note,description,category,account)
            val data = when(result){
                is Result.Failure -> result.data
                is Result.Success -> "Income Added"
            }
            Toast.makeText(application,data,Toast.LENGTH_SHORT).show()
        }
    }

    fun addExpense(date: LocalDate, amount: String, note: String, description: String = "", categoryID: CategoryID, accountID: AccountID) {

        viewModelScope.launch {
            val category: Category? = categoryProvider.expenseCategories.find { it.id == categoryID }
            val account: Account? = accountProvider.accounts.find { it.id == accountID }

            if(category == null) {
                Toast.makeText(application,"Invalid Expense Category $categoryID",Toast.LENGTH_SHORT).show()
                return@launch
            } else if(account == null) {
                Toast.makeText(application,"Invalid Account",Toast.LENGTH_SHORT).show()
                return@launch
            }

            val result: Result = transactionManager.addExpense(date,amount,note,description,category,account)
            val data = when(result){
                is Result.Failure -> result.data
                is Result.Success -> "Expense Added"
            }
            Toast.makeText(application,data,Toast.LENGTH_SHORT).show()
        }
    }

    fun addTransfer(date: LocalDate, amount: String, note: String, description: String = "", fromAccountID: AccountID, toAccountID: AccountID) {

        viewModelScope.launch {
            val from: Account? = accountProvider.accounts.find {  it.id == fromAccountID}
            val to: Account? = accountProvider.accounts.find { it.id == toAccountID}

            if(from == null) {
                Toast.makeText(application,"Invalid Category",Toast.LENGTH_SHORT).show()
                return@launch
            } else if(to == null) {
                Toast.makeText(application,"Invalid Account",Toast.LENGTH_SHORT).show()
                return@launch
            }

            val result: Result = transactionManager.addTransfer(date,amount,note,description,from,to)
            val data = when(result){
                is Result.Failure -> result.data
                is Result.Success -> "Transfer Added"
            }
            Toast.makeText(application,data,Toast.LENGTH_SHORT).show()
        }
    }

    fun updateIncome(transaction: Transaction, date: LocalDate, amount: String, note: String, description: String = "", categoryID: CategoryID, accountID: AccountID) {

        viewModelScope.launch {
            val category: Category? = categoryProvider.incomeCategories.find { it.id == categoryID }
            val account: Account? = accountProvider.accounts.find { it.id == accountID }

            if(category == null) {
                Toast.makeText(application,"Invalid Income Category $categoryID",Toast.LENGTH_SHORT).show()
                return@launch
            } else if(account == null) {
                Toast.makeText(application,"Invalid Account",Toast.LENGTH_SHORT).show()
                return@launch
            }

            when(transaction){
                is Income -> {
                    val result: Result = transactionManager.updateIncome(transaction,date,amount,note,description,category,account)
                    val data = when(result){
                        is Result.Failure -> result.data
                        is Result.Success -> "Income Updated"
                    }
                    Toast.makeText(application,data,Toast.LENGTH_SHORT).show()
                }
                else -> {
                    when(val result: Result = transactionManager.deleteTransaction(transaction)){
                        is Result.Failure -> Toast.makeText(application,result.data,Toast.LENGTH_SHORT).show()
                        is Result.Success -> addIncome(date, amount, note, description, categoryID, accountID)
                    }
                }
            }
        }
    }

    fun updateExpense(transaction: Transaction, date: LocalDate, amount: String, note: String, description: String = "", categoryID: CategoryID, accountID: AccountID) {

        viewModelScope.launch {
            val category: Category? = categoryProvider.expenseCategories.find { it.id == categoryID }
            val account: Account? = accountProvider.accounts.find { it.id == accountID }

            if(category == null) {
                Toast.makeText(application,"Invalid Expense Category $categoryID",Toast.LENGTH_SHORT).show()
                return@launch
            } else if(account == null) {
                Toast.makeText(application,"Invalid Account",Toast.LENGTH_SHORT).show()
                return@launch
            }

            when(transaction){
                is Expense -> {
                    val result: Result = transactionManager.updateExpense(transaction,date,amount,note,description,category,account)
                    val data = when(result){
                        is Result.Failure -> result.data
                        is Result.Success -> "Expense Updated"
                    }
                    Toast.makeText(application,data,Toast.LENGTH_SHORT).show()
                }
                else -> {
                    when(val result: Result = transactionManager.deleteTransaction(transaction)){
                        is Result.Failure -> Toast.makeText(application,result.data,Toast.LENGTH_SHORT).show()
                        is Result.Success -> addExpense(date, amount, note, description, categoryID, accountID)
                    }
                }
            }
        }
    }

    fun updateTransfer(transaction: Transaction, date: LocalDate, amount: String, note: String, description: String = "", fromAccountID: AccountID, toAccountID: AccountID) {

        viewModelScope.launch {
            val from: Account? = accountProvider.accounts.find {  it.id == fromAccountID}
            val to: Account? = accountProvider.accounts.find { it.id == toAccountID}

            if(from == null) {
                Toast.makeText(application,"Invalid From Account",Toast.LENGTH_SHORT).show()
                return@launch
            } else if(to == null) {
                Toast.makeText(application,"Invalid To Account",Toast.LENGTH_SHORT).show()
                return@launch
            }

            when(transaction){
                is Transfer -> {
                    val result: Result = transactionManager.updateTransfer(transaction,date,amount,note,description,from,to)
                    val data = when(result){
                        is Result.Failure -> result.data
                        is Result.Success -> "Transfer Updated"
                    }
                    Toast.makeText(application,data,Toast.LENGTH_SHORT).show()
                }
                else -> {
                    when(val result: Result = transactionManager.deleteTransaction(transaction)){
                        is Result.Failure -> Toast.makeText(application,result.data,Toast.LENGTH_SHORT).show()
                        is Result.Success -> addTransfer(date, amount, note, description, fromAccountID, toAccountID)
                    }
                }
            }
        }
    }

    fun deleteTransaction(transaction: Transaction){
        viewModelScope.launch {
            val result: Result = transactionManager.deleteTransaction(transaction)
            val data = when(result){
                is Result.Failure -> result.data
                is Result.Success -> "Transaction Deleted"
            }
            Toast.makeText(application,data,Toast.LENGTH_SHORT).show()
        }
    }

    fun fetchTransaction(id: TransactionID) {

        viewModelScope.launch {
            val transaction = transactionManager.getTransaction(id)
            _existingTransaction.postValue(transaction)
            if(transaction == null) return@launch
            when(transaction){
                is Expense -> TransactionType.EXPENSE
                is Income -> TransactionType.INCOME
                is Transfer -> TransactionType.TRANSFER
            }.let { _transactionType.postValue(it) }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}