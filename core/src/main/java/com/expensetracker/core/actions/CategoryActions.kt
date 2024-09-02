package com.expensetracker.core.actions


import com.expensetracker.core.models.Category
import com.expensetracker.core.models.CategoryID
import com.expensetracker.core.support.CategoryResponse


interface CategoryActions<T: Category> {
    fun generateId(): CategoryID
    fun hasCategory(categoryID: CategoryID): Boolean
    fun addCategory(category: T): CategoryResponse
    fun getCategory(categoryID: CategoryID): T?
    fun getAllCategories(): List<T>
    fun updateCategory(category: T): CategoryResponse
    fun deleteCategory(id: CategoryID): CategoryResponse
}