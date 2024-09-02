package com.expensetracker.domain.concretes.category

import com.expensetracker.core.actions.ExpenseCategoryActions
import com.expensetracker.core.actions.IncomeCategoryActions
import com.expensetracker.core.models.Category
import com.expensetracker.core.models.ExpenseCategory
import com.expensetracker.core.models.IncomeCategory
import com.expensetracker.core.support.CategoryResponse
import com.expensetracker.core.support.CustomException
import com.expensetracker.core.support.SimpleName
import com.expensetracker.domain.contracts.category.CategoryManager
import com.expensetracker.domain.contracts.category.CategoryProvider
import com.expensetracker.domain.support.Result

class CategoryManagerImp(
    private val categoryProvider: CategoryProvider,
    private val incomeCategoryActions: IncomeCategoryActions,
    private val expenseCategoryActions: ExpenseCategoryActions
): CategoryManager,
CategoryProvider by categoryProvider {
    override fun addIncomeCategory(name: String) : Result {
        try {
            val id = incomeCategoryActions.generateId()
            val simpleName = SimpleName(name)
            val result = incomeCategoryActions.addCategory(IncomeCategory(id,simpleName))
            return if(result == CategoryResponse.CATEGORY_CREATED){
                Result.Success(result.toString())
            } else Result.Failure(result.toString())
        } catch (e: CustomException) {
            return Result.Failure(e.message)
        }
    }
    override fun addExpenseCategory(name: String) : Result {
        try {
            val id = expenseCategoryActions.generateId()
            val simpleName = SimpleName(name)
            val result = expenseCategoryActions.addCategory(ExpenseCategory(id,simpleName))
            return if(result == CategoryResponse.CATEGORY_CREATED){
                Result.Success(result.toString())
            } else Result.Failure(result.toString())
        } catch (e: CustomException) {
            return Result.Failure(e.message)
        }
    }
    override fun update(category: Category, name: String): Result {
        try {
            val simpleName = SimpleName(name)
            val result = when(category){
                is ExpenseCategory -> expenseCategoryActions.updateCategory( category = category.copy(name = simpleName))
                is IncomeCategory -> incomeCategoryActions.updateCategory( category = category.copy(name = simpleName))
            }
            return if(result == CategoryResponse.CATEGORY_UPDATED){
                Result.Success(result.toString())
            } else Result.Failure(result.toString())
        } catch (e: CustomException) {
            return Result.Failure(e.message)
        }
    }
    override fun delete(category: Category): Result {
        val result = incomeCategoryActions.deleteCategory(category.id)
        return if(result == CategoryResponse.CATEGORY_DELETED){
            Result.Success(result.toString())
        } else Result.Failure(result.toString())
    }
}