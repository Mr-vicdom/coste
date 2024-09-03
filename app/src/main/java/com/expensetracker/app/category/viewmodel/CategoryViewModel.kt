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
import com.expensetracker.core.support.SimpleName
import com.expensetracker.domain.contracts.category.CategoryManager
import com.expensetracker.domain.support.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryViewModel(application: Application): AndroidViewModel(application) {

    private val categoryManager: CategoryManager by lazy {
        DataHandler(application).categoryManager
    }

    private val categories: MutableList<Category> = mutableListOf()


    private val updatedCategoriesMap: MutableMap<Int, Category> = mutableMapOf()

    private val updatedCategories: List<Category>
        get() = updatedCategoriesMap.values.toList()

    private val _categoryList: MutableLiveData<List<Category>> = MutableLiveData()

    private val _oldCategoryList: MutableLiveData<List<Category>> = MutableLiveData()

    val categoryList: LiveData<List<Category>>
        get() = _categoryList


    val oldCategoryList: LiveData<List<Category>>
        get() = _oldCategoryList

    fun fetchCategories(type: CategoryType = CategoryType.INCOME_CATEGORY) {
        viewModelScope.launch(Dispatchers.IO) {
            when (type){
               CategoryType.INCOME_CATEGORY -> categoryManager.incomeCategories
               CategoryType.EXPENSE_CATEGORY -> categoryManager.expenseCategories
            }.let { categories1 ->
                categories.clear()
                categories.addAll(categories1)
                updatedCategoriesMap.clear()
                updatedCategoriesMap.putAll(categories1.associateBy { it.id })
            }

            Log.d("=>log", "fetchCategories: ${updatedCategories}")

            _categoryList.postValue(updatedCategories)
        }
    }

    fun getOldCategories() {
        viewModelScope.launch {
            _oldCategoryList.postValue(categories)
        }
    }

    fun updateCategory(text: String, category: Category) {
        val cate = updatedCategoriesMap[category.id]
        if (cate != null){
            try {
                val name = SimpleName(text)
                val updatedCategory = when (cate) {
                    is ExpenseCategory -> cate.copy(name = name)
                    is IncomeCategory -> cate.copy(name = name)
                }
                updatedCategoriesMap[category.id] = updatedCategory
            } catch (_ : Exception) {
                Toast.makeText(getApplication(), "Category Name Invalid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            categories.forEach { category ->
                if(updatedCategoriesMap.contains(category.id)){
                    val updatedCategory = updatedCategoriesMap[category.id]
                    if(updatedCategory != null && updatedCategory.name != category.name){
                        categoryManager.update(category, updatedCategory.name.toString())
                    }
                }
            }
        }
    }

    fun deleteCategory(category: Category){
        viewModelScope.launch(Dispatchers.IO) {
            categoryManager.delete(category).let {
                withContext(Dispatchers.Main){
                    Toast.makeText(getApplication(), it.data, Toast.LENGTH_SHORT).show()
                    if (it is Result.Success){
                        categories.removeIf { it.id == category.id }
                        updatedCategoriesMap.remove(category.id)
                        _categoryList.postValue(updatedCategories)
                    }
                }
            }
        }
    }
}