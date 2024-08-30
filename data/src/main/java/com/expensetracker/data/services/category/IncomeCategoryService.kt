package com.expensetracker.data.services.category

import com.expensetracker.core.actions.IncomeCategoryActions
import com.expensetracker.core.models.CategoryID
import com.expensetracker.core.models.IncomeCategory
import com.expensetracker.data.services.support.IdGenerator

class IncomeCategoryService(
    private val incomeCategories: MutableMap<CategoryID,IncomeCategory>,
    idGenerator: IdGenerator
): IncomeCategoryActions ,
    CategoryService<IncomeCategory>(incomeCategories, idGenerator)