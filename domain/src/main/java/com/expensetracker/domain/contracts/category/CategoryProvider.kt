package com.expensetracker.domain.contracts.category

import com.expensetracker.core.models.Category
import com.expensetracker.core.models.ExpenseCategory
import com.expensetracker.core.models.IncomeCategory

interface CategoryProvider {
    val incomeCategories: List<IncomeCategory>
    val expenseCategories: List<ExpenseCategory>
}