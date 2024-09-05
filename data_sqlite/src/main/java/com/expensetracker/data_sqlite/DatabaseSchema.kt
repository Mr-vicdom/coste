package com.expensetracker.data_sqlite

import android.provider.BaseColumns


object DatabaseSchema {

    object IncomeTable : BaseColumns {
        const val TABLE_NAME = "incomes"
        const val ID = "id"
        const val DATE = "date"
        const val AMOUNT = "amount"
        const val NOTE = "note"
        const val DESCRIPTION = "description"
        const val CATEGORY_ID = "category_id"
        const val ACCOUNT_ID = "account_id"

        val columns: Array<String> = arrayOf(
                ID, DATE, AMOUNT, NOTE, DESCRIPTION, CATEGORY_ID, ACCOUNT_ID
            )
    }

    object ExpenseTable : BaseColumns {
        const val TABLE_NAME = "expenses"
        const val ID = "id"
        const val DATE = "date"
        const val AMOUNT = "amount"
        const val NOTE = "note"
        const val DESCRIPTION = "description"
        const val CATEGORY_ID = "category_id"
        const val ACCOUNT_ID = "account_id"

        val columns: Array<String> = arrayOf(
                ID, DATE, AMOUNT, NOTE, DESCRIPTION, CATEGORY_ID, ACCOUNT_ID
            )
    }

    object TransferTable : BaseColumns {
        const val TABLE_NAME = "transfers"
        const val ID = "id"
        const val DATE = "date"
        const val AMOUNT = "amount"
        const val NOTE = "note"
        const val DESCRIPTION = "description"
        const val FROM_ACCOUNT = "from_account"
        const val TO_ACCOUNT = "to_account"

        val columns: Array<String> = arrayOf(
                ID, DATE, AMOUNT, NOTE, DESCRIPTION, FROM_ACCOUNT, TO_ACCOUNT
            )
    }

    object BankAccountTable : BaseColumns {
        const val TABLE_NAME = "bank_accounts"
        const val ID = "id"
        const val NAME = "name"
        const val BALANCE = "balance"
        const val MINIMUM_BALANCE = "minimum_balance"

        val columns: Array<String> = arrayOf(
                ID, NAME, BALANCE, MINIMUM_BALANCE
            )
    }

    object CashAccountTable : BaseColumns {
        const val TABLE_NAME = "cash_accounts"
        const val ID = "id"
        const val NAME = "name"
        const val BALANCE = "balance"
        const val MINIMUM_BALANCE = "minimum_balance"

        val columns: Array<String> = arrayOf(
                ID, NAME, BALANCE, MINIMUM_BALANCE
            )
    }

    object CreditCardTable : BaseColumns {
        const val TABLE_NAME = "credit_cards"
        const val ID = "id"
        const val NAME = "name"
        const val BALANCE = "balance"
        const val OUT_STANDING = "out_standings"

        val columns: Array<String> = arrayOf(
                ID, NAME, BALANCE, OUT_STANDING
            )
    }

    object DebitCardTable : BaseColumns {
        const val TABLE_NAME = "debit_cards"
        const val ID = "id"
        const val NAME = "name"
        const val BALANCE = "balance"
        const val BANK_ACCOUNT_ID = "bank_account_id"
        const val DEBIT_LIMIT = "debit_limit"

        val columns: Array<String> = arrayOf(
                ID, NAME, BALANCE, BANK_ACCOUNT_ID, DEBIT_LIMIT
            )
    }

    object CategoryTable : BaseColumns {
        const val TABLE_NAME = "categories"
        const val ID = "id"
        const val NAME = "name"
        const val IS_INCOME = "is_income"

        val columns: Array<String> = arrayOf(
                ID, NAME
            )
    }
    
    object IdStoreTable : BaseColumns {
        const val TABLE_NAME = "id_store"
        const val LAST_ACCOUNT_ID = "account_id"
        const val LAST_CATEGORY_ID = "category_id"
        const val LAST_TRANSACTION_ID = "transaction_id"
        
        val columns: Array<String> = arrayOf(
                LAST_ACCOUNT_ID,
                LAST_CATEGORY_ID,
                LAST_TRANSACTION_ID
            )
    }
}