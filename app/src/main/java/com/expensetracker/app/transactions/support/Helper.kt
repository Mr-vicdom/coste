package com.expensetracker.app.transactions.support

import com.expensetracker.core.support.Literals.ACCOUNT_ID
import com.expensetracker.core.support.Literals.AMOUNT
import com.expensetracker.core.support.Literals.CATEGORY_ID
import com.expensetracker.core.support.Literals.DATE
import com.expensetracker.core.support.Literals.DESCRIPTION
import com.expensetracker.core.support.Literals.NOTE
import com.expensetracker.core.support.Literals.TRANSACTION_ID

object Literals {
    const val DATE_LABEL = DATE
    const val AMOUNT_LABEL = AMOUNT
    const val CATEGORY_ID_LABEL = CATEGORY_ID
    const val ACCOUNT_ID_LABEL = ACCOUNT_ID
    const val TRANSACTION_ID_LABEL = TRANSACTION_ID
    const val FROM_ACCOUNT_ID_LABEL = "From$ACCOUNT_ID"
    const val TO_ACCOUNT_ID_LABEL = "To$ACCOUNT_ID"
    const val NOTE_LABEL = NOTE
    const val DESCRIPTION_LABEL = DESCRIPTION
    const val MODIFY_TRANSACTION = "ModifyTransaction"
    const val TRANSACTION_TYPE = "TransactionType"

    const val FILTER_ACCOUNT_IDS_LABEL = "FilterAccountIds"
    const val MONTH_LABEL = "Month"
    const val YEAR_LABEL = "Year"
}