package com.expensetracker.app.accounts.support

import com.expensetracker.core.models.Account

sealed class AccountListData {
    data class AccountTitle(val title: String): AccountListData()
    data class AccountItem(val account: Account): AccountListData()
}