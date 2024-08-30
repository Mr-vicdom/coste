package com.expensetracker.domain.concretes.category

import com.expensetracker.core.actions.CategoryActions
import com.expensetracker.core.actions.ExpenseCategoryActions
import com.expensetracker.core.actions.IncomeCategoryActions
import com.expensetracker.core.models.Category
import com.expensetracker.core.models.ExpenseCategory
import com.expensetracker.core.models.IncomeCategory
import com.expensetracker.domain.contracts.category.CategoryProvider

class CategoryProviderImp(
    private val incomeCategoryActions: IncomeCategoryActions,
    private val expenseCategoryActions: ExpenseCategoryActions
): CategoryProvider {
    override val incomeCategories: List<IncomeCategory>
        get() = incomeCategoryActions.getAllCategories()
    override val expenseCategories: List<ExpenseCategory>
        get() = expenseCategoryActions.getAllCategories()
}