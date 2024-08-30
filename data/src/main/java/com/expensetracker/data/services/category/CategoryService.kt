package com.expensetracker.data.services.category

import com.expensetracker.core.actions.CategoryActions
import com.expensetracker.core.models.Category
import com.expensetracker.core.models.CategoryID
import com.expensetracker.core.support.CategoryResponse
import com.expensetracker.data.services.support.IdGenerator
import com.expensetracker.data.services.support.Literals

abstract class CategoryService<T: Category>(
    private val categories: MutableMap<CategoryID,T>,
    private val idGenerator: IdGenerator,
): CategoryActions<T> {
    override fun generateId(): CategoryID = idGenerator.newId

    override fun addCategory(category: T): CategoryResponse {
        if(categories.contains(category.id)) return CategoryResponse.CATEGORY_ALREADY_EXIST
        categories[category.id] = category
        return CategoryResponse.CATEGORY_CREATED
    }

    override fun getCategory(categoryID: CategoryID): T? = categories[categoryID]

    override fun getAllCategories(): List<T> = categories.values.toList()

    override fun updateCategory(id: CategoryID, category: T): CategoryResponse {
        if(categories.contains(id)){
            categories[id] = category
            return CategoryResponse.CATEGORY_UPDATED
        } else return CategoryResponse.CATEGORY_NOT_EXIST
    }

    override fun deleteCategory(id: CategoryID): CategoryResponse {
        return categories.remove(id).let {
            if(it == null)
                CategoryResponse.CATEGORY_NOT_EXIST
            else
                CategoryResponse.CATEGORY_DELETED
        }
    }
}