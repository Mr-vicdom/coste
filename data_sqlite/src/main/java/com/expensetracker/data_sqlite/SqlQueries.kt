package com.expensetracker.data_sqlite

object SQLQueries {
    const val CREATE_INCOMES_TABLE = "CREATE TABLE ${DatabaseSchema.IncomeTable.TABLE_NAME} (" +
            "${DatabaseSchema.IncomeTable.ID} INTEGER PRIMARY KEY, " +
            "${DatabaseSchema.IncomeTable.DATE} BIGINT, " +
            "${DatabaseSchema.IncomeTable.AMOUNT} DOUBLE, " +
            "${DatabaseSchema.IncomeTable.NOTE} VARCHAR(30), " +
            "${DatabaseSchema.IncomeTable.DESCRIPTION} TEXT, " +
            "${DatabaseSchema.IncomeTable.CATEGORY_ID} INTEGER, " +
            "${DatabaseSchema.IncomeTable.ACCOUNT_ID} INTEGER, " +
            "FOREIGN KEY(${DatabaseSchema.IncomeTable.CATEGORY_ID}) " +
            " REFERENCES ${DatabaseSchema.CategoryTable.TABLE_NAME}(${DatabaseSchema.CategoryTable.ID})" +
            ");"


    const val CREATE_EXPENSES_TABLE = "CREATE TABLE ${DatabaseSchema.ExpenseTable.TABLE_NAME} (" +
            "${DatabaseSchema.ExpenseTable.ID} INTEGER PRIMARY KEY," +
            "${DatabaseSchema.ExpenseTable.DATE} BIGINT," +
            "${DatabaseSchema.ExpenseTable.AMOUNT} DOUBLE," +
            "${DatabaseSchema.ExpenseTable.NOTE} VARCHAR(30)," +
            "${DatabaseSchema.ExpenseTable.DESCRIPTION} TEXT," +
            "${DatabaseSchema.ExpenseTable.CATEGORY_ID} INTEGER," +
            "${DatabaseSchema.ExpenseTable.ACCOUNT_ID} INTEGER," +
            "FOREIGN KEY(${DatabaseSchema.ExpenseTable.CATEGORY_ID}) " +
            " REFERENCES ${DatabaseSchema.CategoryTable.TABLE_NAME}(${DatabaseSchema.CategoryTable.ID})" +
            ");"

    const val CREATE_TRANSFERS_TABLE = "CREATE TABLE ${DatabaseSchema.TransferTable.TABLE_NAME} (" +
            "${DatabaseSchema.TransferTable.ID} INTEGER PRIMARY KEY," +
            "${DatabaseSchema.TransferTable.DATE} BIGINT," +
            "${DatabaseSchema.TransferTable.AMOUNT} DOUBLE," +
            "${DatabaseSchema.TransferTable.NOTE} VARCHAR(30)," +
            "${DatabaseSchema.TransferTable.DESCRIPTION} TEXT," +
            "${DatabaseSchema.TransferTable.FROM_ACCOUNT} INTEGER," +
            "${DatabaseSchema.TransferTable.TO_ACCOUNT} INTEGER" +
            ");"

    const val CREATE_BANK_ACCOUNTS_TABLE = "CREATE TABLE ${DatabaseSchema.BankAccountTable.TABLE_NAME} (" +
            "${DatabaseSchema.BankAccountTable.ID} INTEGER PRIMARY KEY," +
            "${DatabaseSchema.BankAccountTable.NAME} VARCHAR(30)," +
            "${DatabaseSchema.BankAccountTable.BALANCE} DOUBLE," +
            "${DatabaseSchema.BankAccountTable.MINIMUM_BALANCE} DOUBLE" +
            ");"

    const val CREATE_CASH_ACCOUNTS_TABLE = "CREATE TABLE ${DatabaseSchema.CashAccountTable.TABLE_NAME} (" +
            "${DatabaseSchema.CashAccountTable.ID} INTEGER PRIMARY KEY," +
            "${DatabaseSchema.CashAccountTable.NAME} VARCHAR(30)," +
            "${DatabaseSchema.CashAccountTable.BALANCE} DOUBLE," +
            "${DatabaseSchema.CashAccountTable.MINIMUM_BALANCE} DOUBLE" +
            ");"

    const val CREATE_CREDIT_CARDS_TABLE = "CREATE TABLE ${DatabaseSchema.CreditCardTable.TABLE_NAME} (" +
            "${DatabaseSchema.CreditCardTable.ID} INTEGER PRIMARY KEY," +
            "${DatabaseSchema.CreditCardTable.NAME} VARCHAR(30)," +
            "${DatabaseSchema.CreditCardTable.BALANCE} DOUBLE," +
            "${DatabaseSchema.CreditCardTable.OUT_STANDING} DOUBLE" +
            ");"

    const val CREATE_DEBIT_CARDS_TABLE = "CREATE TABLE ${DatabaseSchema.DebitCardTable.TABLE_NAME} (" +
            "${DatabaseSchema.DebitCardTable.ID} INTEGER PRIMARY KEY," +
            "${DatabaseSchema.DebitCardTable.BANK_ACCOUNT_ID} INTEGER," +
            "${DatabaseSchema.DebitCardTable.NAME} VARCHAR(30)," +
            "${DatabaseSchema.DebitCardTable.BALANCE} DOUBLE," +
            "${DatabaseSchema.DebitCardTable.DEBIT_LIMIT} DOUBLE," +
            "FOREIGN KEY(${DatabaseSchema.DebitCardTable.BANK_ACCOUNT_ID}) " +
            " REFERENCES ${DatabaseSchema.BankAccountTable.TABLE_NAME}(${DatabaseSchema.BankAccountTable.ID})" +
            ");"

    const val CREATE_CATEGORY_TABLE = "CREATE TABLE ${DatabaseSchema.CategoryTable.TABLE_NAME} (" +
            "${DatabaseSchema.CategoryTable.ID} INTEGER PRIMARY KEY," +
            "${DatabaseSchema.CategoryTable.NAME} VARCHAR(30)," +
            "${DatabaseSchema.CategoryTable.IS_INCOME} BOOLEAN" +
            ");"

    const val CREATE_ID_STORE_TABLE = "CREATE TABLE ${DatabaseSchema.IdStoreTable.TABLE_NAME} (" +
            "${DatabaseSchema.IdStoreTable.LAST_CATEGORY_ID} INTEGER," +
            "${DatabaseSchema.IdStoreTable.LAST_ACCOUNT_ID} INTEGER," +
            "${DatabaseSchema.IdStoreTable.LAST_TRANSACTION_ID} INTEGER" +
            ");"

    const val DROP_INCOMES_TABLE = "DROP TABLE IF EXISTS ${DatabaseSchema.IncomeTable.TABLE_NAME};"
    const val DROP_EXPENSES_TABLE = "DROP TABLE IF EXISTS ${DatabaseSchema.ExpenseTable.TABLE_NAME};"
    const val DROP_TRANSFERS_TABLE = "DROP TABLE IF EXISTS ${DatabaseSchema.TransferTable.TABLE_NAME};"
    const val DROP_BANK_ACCOUNTS_TABLE = "DROP TABLE IF EXISTS ${DatabaseSchema.BankAccountTable.TABLE_NAME};"
    const val DROP_CASH_ACCOUNTS_TABLE = "DROP TABLE IF EXISTS ${DatabaseSchema.CashAccountTable.TABLE_NAME};"
    const val DROP_CREDIT_CARDS_TABLE = "DROP TABLE IF EXISTS ${DatabaseSchema.CreditCardTable.TABLE_NAME};"
    const val DROP_DEBIT_CARDS_TABLE = "DROP TABLE IF EXISTS ${DatabaseSchema.DebitCardTable.TABLE_NAME};"
    const val DROP_CATEGORY_TABLE = "DROP TABLE IF EXISTS ${DatabaseSchema.CategoryTable.TABLE_NAME};"
    const val DROP_ID_STORE_TABLE = "DROP TABLE IF EXISTS ${DatabaseSchema.IdStoreTable.TABLE_NAME};"

}