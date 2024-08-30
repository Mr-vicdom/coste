package com.expensetracker.data.services.category

import com.expensetracker.core.actions.ExpenseActions
import com.expensetracker.core.actions.ExpenseCategoryActions
import com.expensetracker.core.models.CategoryID
import com.expensetracker.core.models.ExpenseCategory
import com.expensetracker.data.services.support.IdGenerator

class ExpenseCategoryService(
    private val expenseCategories: MutableMap<CategoryID, ExpenseCategory>,
    idGenerator: IdGenerator
): ExpenseCategoryActions,
CategoryService<ExpenseCategory>(expenseCategories,idGenerator)