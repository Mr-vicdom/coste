package com.expensetracker.core.models

import com.expensetracker.core.support.SimpleName

typealias CategoryID = Int

sealed class Category(open val id: CategoryID, open val name: SimpleName)

data class IncomeCategory(override val id: CategoryID, override val name: SimpleName): Category(id, name)
data class ExpenseCategory(override val id: CategoryID, override val name: SimpleName): Category(id, name)