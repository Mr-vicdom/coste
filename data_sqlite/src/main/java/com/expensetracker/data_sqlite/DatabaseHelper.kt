package com.expensetracker.data_sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.SQLException


class DatabaseHelper(val context: Context): SQLiteOpenHelper(context, DB_NAME,null, DB_VERSION) {

    init {

    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.let {
                it.execSQL(SQLQueries.CREATE_CATEGORY_TABLE)
                it.execSQL(SQLQueries.CREATE_BANK_ACCOUNTS_TABLE)
                it.execSQL(SQLQueries.CREATE_CASH_ACCOUNTS_TABLE)
                it.execSQL(SQLQueries.CREATE_CREDIT_CARDS_TABLE)
                it.execSQL(SQLQueries.CREATE_DEBIT_CARDS_TABLE)
                it.execSQL(SQLQueries.CREATE_INCOMES_TABLE)
                it.execSQL(SQLQueries.CREATE_EXPENSES_TABLE)
                it.execSQL(SQLQueries.CREATE_TRANSFERS_TABLE)
            }
        } catch (e: SQLException) {
            Log.d("=>log", "onCreate: $e")
        }
        Log.d("=>log", "onCreate: $db")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.let {
            it.execSQL(SQLQueries.DROP_CATEGORY_TABLE)
            it.execSQL(SQLQueries.DROP_BANK_ACCOUNTS_TABLE)
            it.execSQL(SQLQueries.DROP_CASH_ACCOUNTS_TABLE)
            it.execSQL(SQLQueries.DROP_CREDIT_CARDS_TABLE)
            it.execSQL(SQLQueries.DROP_DEBIT_CARDS_TABLE)
            it.execSQL(SQLQueries.DROP_INCOMES_TABLE)
            it.execSQL(SQLQueries.DROP_EXPENSES_TABLE)
            it.execSQL(SQLQueries.DROP_TRANSFERS_TABLE)
        }

        onCreate(db)
    }

    companion object {
        const val DB_NAME = "coste"
        const val DB_VERSION = 1
    }
}