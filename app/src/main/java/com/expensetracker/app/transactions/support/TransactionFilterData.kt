package com.expensetracker.app.transactions.support

import com.expensetracker.core.models.Account
import com.expensetracker.core.models.AccountID

sealed class TransactionFilterData {
    data class TransactionFilterTitle(val title: String) : TransactionFilterData()
    data class TransactionFilterItem(
        val accountID: AccountID,
        val label: String,
        val income: Double = 0.0,
        val expense: Double = 0.0,
        val transferIn: Double = 0.0,
        val transferOut: Double = 0.0,
    ): TransactionFilterData()
}