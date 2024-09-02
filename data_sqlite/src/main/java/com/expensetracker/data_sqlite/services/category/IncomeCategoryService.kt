package com.expensetracker.data_sqlite.services.category

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.expensetracker.core.actions.IncomeCategoryActions
import com.expensetracker.core.models.CategoryID
import com.expensetracker.core.models.IncomeCategory
import com.expensetracker.core.support.CategoryResponse
import com.expensetracker.core.support.SimpleName
import com.expensetracker.data_sqlite.DatabaseSchema
import com.expensetracker.data_sqlite.services.support.IdGenerator

class IncomeCategoryService(
    private val db: SQLiteDatabase,
    private val idGenerator: IdGenerator,
    private val schema: DatabaseSchema.CategoryTable
) : IncomeCategoryActions {

    override fun generateId(): CategoryID {
        return idGenerator.newId
    }

    override fun hasCategory(categoryID: CategoryID): Boolean {
        return try {
            val cursor = db.query(
                schema.TABLE_NAME,
                arrayOf(schema.ID),
                "${schema.ID} = ? AND ${schema.IS_INCOME} = ?",
                arrayOf(categoryID.toString(), "1"),
                null,
                null,
                null
            )
            cursor.use {
                it.moveToFirst()
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
            false
        }
    }

    override fun addCategory(category: IncomeCategory): CategoryResponse {
        return try {
            db.beginTransaction()
            if (hasCategory(category.id)) {
                db.setTransactionSuccessful()
                return CategoryResponse.CATEGORY_ALREADY_EXIST
            }
            val contentValues = ContentValues().apply {
                put(schema.ID, category.id)
                put(schema.NAME, category.name.value)
                put(schema.IS_INCOME, true)
            }
            val result = db.insertOrThrow(schema.TABLE_NAME, null, contentValues)
            if (result != -1L) {
                db.setTransactionSuccessful()
                CategoryResponse.CATEGORY_CREATED
            } else {
                CategoryResponse.CATEGORY_NOT_CREATED
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
            CategoryResponse.CATEGORY_NOT_CREATED
        } finally {
            db.endTransaction()
        }
    }

    override fun getCategory(categoryID: CategoryID): IncomeCategory? {
        return try {
            val cursor = db.query(
                schema.TABLE_NAME,
                schema.columns,
                "${schema.ID} = ? AND ${schema.IS_INCOME} = ?",
                arrayOf(categoryID.toString(), "1"),
                null,
                null,
                null
            )
            cursor.use {
                if (cursor.moveToFirst()) {
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(schema.NAME))
                    IncomeCategory(categoryID, SimpleName(name))
                } else {
                    null
                }
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
            null
        }
    }

    override fun getAllCategories(): List<IncomeCategory> {
        val categories = mutableListOf<IncomeCategory>()
        return try {
            val cursor = db.query(
                schema.TABLE_NAME,
                schema.columns,
                "${schema.IS_INCOME} = ?",
                arrayOf("1"),
                null,
                null,
                null
            )
            cursor.use {
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(schema.ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(schema.NAME))
                    categories.add(IncomeCategory(id, SimpleName(name)))
                }
            }
            categories
        } catch (e: SQLiteException) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun updateCategory(category: IncomeCategory): CategoryResponse {
        return try {
            db.beginTransaction()
            val contentValues = ContentValues().apply {
                put(schema.NAME, category.name.value)
            }
            val result = db.update(
                schema.TABLE_NAME,
                contentValues,
                "${schema.ID} = ? AND ${schema.IS_INCOME} = ?",
                arrayOf(category.id.toString(), "1")
            )
            if (result > 0) {
                db.setTransactionSuccessful()
                CategoryResponse.CATEGORY_UPDATED
            } else {
                CategoryResponse.CATEGORY_NOT_EXIST
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
            CategoryResponse.CATEGORY_NOT_EXIST
        } finally {
            db.endTransaction()
        }
    }

    override fun deleteCategory(id: CategoryID): CategoryResponse {
        return try {
            db.beginTransaction()
            val result = db.delete(
                schema.TABLE_NAME,
                "${schema.ID} = ? AND ${schema.IS_INCOME} = ?",
                arrayOf(id.toString(), "1")
            )
            if (result > 0) {
                db.setTransactionSuccessful()
                CategoryResponse.CATEGORY_DELETED
            } else {
                CategoryResponse.CATEGORY_NOT_EXIST
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
            CategoryResponse.CATEGORY_NOT_EXIST
        } finally {
            db.endTransaction()
        }
    }
}
