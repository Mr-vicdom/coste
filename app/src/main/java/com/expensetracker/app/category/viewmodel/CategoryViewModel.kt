package com.expensetracker.app.category.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.data.DataHandler
import com.expensetracker.core.models.Category
import com.expensetracker.core.models.ExpenseCategory
import com.expensetracker.core.models.IncomeCategory
import com.expensetracker.core.support.CategoryType
import com.expensetracker.domain.contracts.category.CategoryManager
import com.expensetracker.domain.support.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val categoryManager: CategoryManager by lazy {
        DataHandler(application).categoryManager
    }

    val incomeCategoriesList: MutableList<Pair<IncomeCategory, Boolean>> = mutableListOf()

    val expenseCategoriesList: MutableList<Pair<ExpenseCategory, Boolean>> = mutableListOf()

    private val _addPosition: MutableLiveData<Int> = MutableLiveData()

    private val _updatePosition: MutableLiveData<Int> = MutableLiveData()

    private val _deletePosition: MutableLiveData<Int> = MutableLiveData()

    val updatePosition: LiveData<Int>
        get() = _updatePosition

    val deletePosition: LiveData<Int>
        get() = _deletePosition

    val addPosition: LiveData<Int>
        get() = _addPosition

    private fun initializeCategoryLists(type: CategoryType, list: List<Category>) {
        when(type){
            CategoryType.INCOME_CATEGORY -> {
                if (incomeCategoriesList.isEmpty())
                    incomeCategoriesList.addAll(list.filterIsInstance<IncomeCategory>().map{ it to false }
                        .toMutableList())
                else {
                    val newList = list.filterIsInstance<IncomeCategory>()
                        .filter { e -> incomeCategoriesList.none { b -> b.first.id == e.id } }
                        .map { it to false }
                    if (newList.isNotEmpty()) {
                        incomeCategoriesList.addAll(newList)
                    }
                }
                _addPosition.postValue(incomeCategoriesList.size)
            }
            CategoryType.EXPENSE_CATEGORY -> {
                if (expenseCategoriesList.isEmpty())
                    expenseCategoriesList.addAll(list.filterIsInstance<ExpenseCategory>().map{ it to false }
                        .toMutableList())
                else {
                    val newList = list.filterIsInstance<ExpenseCategory>()
                        .filter { e -> expenseCategoriesList.none { b -> b.first.id == e.id } }
                        .map { it to false }
                    if (newList.isNotEmpty()) {
                        expenseCategoriesList.addAll(newList)
                    }
                }
                _addPosition.postValue(expenseCategoriesList.size)
            }
        }
    }

    fun fetchCategories(type: CategoryType = CategoryType.INCOME_CATEGORY) {
        viewModelScope.launch(Dispatchers.IO) {
            when (type) {
                CategoryType.INCOME_CATEGORY -> categoryManager.incomeCategories
                CategoryType.EXPENSE_CATEGORY -> categoryManager.expenseCategories
            }.let { categories1 ->
                initializeCategoryLists(type,categories1)
            }
        }
    }

    fun addCategory(text: String, type: CategoryType) {
        viewModelScope.launch(Dispatchers.IO) {
            when (type) {
                CategoryType.INCOME_CATEGORY -> categoryManager.addIncomeCategory(text)
                CategoryType.EXPENSE_CATEGORY -> categoryManager.addExpenseCategory(text)
            }.let {
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), it.data, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun updateCategory(text: String, category: Category, position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            when (category) {
                is ExpenseCategory -> categoryManager.update(category, text)
                is IncomeCategory -> categoryManager.update(category, text)
            }.let { result ->
                val updatedCategory: Category? = when(category){
                    is ExpenseCategory -> categoryManager.expenseCategories.firstOrNull{ it.id == category.id}
                    is IncomeCategory -> categoryManager.incomeCategories.firstOrNull{ it.id == category.id}
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), result.data, Toast.LENGTH_SHORT).show()
                    if (result is Result.Success) {
                        if (updatedCategory != null) {
                            Log.d("=>log", "updateCategory1: $category")
                            when(updatedCategory){
                                is ExpenseCategory -> expenseCategoriesList[position] = updatedCategory to false
                                is IncomeCategory -> incomeCategoriesList[position] = updatedCategory to false
                            }
                        }
                        _updatePosition.postValue(position)
                    }
                }
            }
        }
    }


    fun deleteCategory(category: Category, position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryManager.delete(category).let {
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), it.data, Toast.LENGTH_SHORT).show()
                    if (it is Result.Success) {
                        when(category){
                            is ExpenseCategory -> if(expenseCategoriesList.indices.contains(position)) expenseCategoriesList.removeAt(position)
                            is IncomeCategory ->  if(incomeCategoriesList.indices.contains(position)) incomeCategoriesList.removeAt(position)
                        }
                        _deletePosition.postValue(position)
                    }
                }
            }
        }
    }
}