package com.expensetracker.domain.contracts.category

import com.expensetracker.core.models.Category
import com.expensetracker.domain.support.Result

interface CategoryManager: CategoryProvider {
    fun addIncomeCategory(name: String): Result
    fun addExpenseCategory(name: String): Result
    fun update(category: Category, name: String): Result
    fun delete(category: Category): Result
}