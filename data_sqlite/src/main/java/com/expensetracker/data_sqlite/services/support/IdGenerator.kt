package com.expensetracker.data_sqlite.services.support

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.util.Log
import com.expensetracker.core.support.AccountResponse
import com.expensetracker.data_sqlite.DatabaseSchema

class IdGenerator(private val default: Int = 0, private val onNewID: (Int)-> Unit = {}) {
    var newId : Int = default
        get() {
            onNewID(field+1)
            return field++
        }
        private set
}

class IdStoreMaster(private val db: SQLiteDatabase, private val schema: DatabaseSchema.IdStoreTable = DatabaseSchema.IdStoreTable){

    private var categoryID: Int = 0
    private var accountID: Int = 0
    private var transactionID: Int = 0

    val forCategory: IdGenerator
        get() = IdGenerator(categoryID){
            setDbValue(it,schema.LAST_CATEGORY_ID)
        }

    val forAccount: IdGenerator
        get() = IdGenerator(accountID){
            setDbValue(it,schema.LAST_ACCOUNT_ID)
        }

    val forTransaction: IdGenerator
        get() = IdGenerator(transactionID){
            setDbValue(it,schema.LAST_TRANSACTION_ID)
        }

    init {
        fetchIdStore()
    }

    private fun setDbValue(value: Int, column: String) {
        try {
            val contentValues = ContentValues().apply {
                put(column, value)
            }

            val result = db.update(
                schema.TABLE_NAME,
                contentValues,
                null,
                null
            )
            if (result > 0) {
                Log.d("=>datalog", "setDbValue: Done $value")
            } else {
                Log.d("=>datalog", "setDbValue: Failed $value")
            }
        } catch (e: Exception){
            Log.d("=>datalog", "setDbValue: Err Thrown $value \n\n")
            Log.e("=>datalog", "setDbValue: $e\n\n" )
        }
    }

    private fun fetchIdStore() {
        try {
            val cursor = db.query(
                schema.TABLE_NAME,
                schema.columns,
                null,
                null,
                null,
                null,
                null
            )

            if (cursor.moveToFirst()){
                categoryID = cursor.getInt(cursor.getColumnIndexOrThrow(schema.LAST_CATEGORY_ID))
                accountID = cursor.getInt(cursor.getColumnIndexOrThrow(schema.LAST_ACCOUNT_ID))
                transactionID = cursor.getInt(cursor.getColumnIndexOrThrow(schema.LAST_TRANSACTION_ID))
                Log.d("=>datalog", "fetchedIdStore: Success ")
            } else {
                if(initialiseTable())
                    fetchIdStore()
                else
                    Log.d("=>datalog", "fetchedIdStore: Failed to init ")
            }
        } catch (e: Exception) {
            Log.d("=>datalog", "fetchIdStore: Error thrown \n\n")
            Log.e("=>datalog", "fetchIdStore: $e\n\n" )
        }
    }

    private fun initialiseTable(): Boolean {
        val contentValues = ContentValues().apply {
            put(schema.LAST_CATEGORY_ID, 0)
            put(schema.LAST_ACCOUNT_ID, 0)
            put(schema.LAST_TRANSACTION_ID, 0)
        }
        val result = db.insert(schema.TABLE_NAME, null, contentValues)
        return result != -1L
    }
}