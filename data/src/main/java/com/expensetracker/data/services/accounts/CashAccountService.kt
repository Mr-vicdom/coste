package com.expensetracker.data.services.accounts

import com.expensetracker.core.actions.CashAccountActions
import com.expensetracker.core.models.CashAccount
import com.expensetracker.core.models.CashAccountID
import com.expensetracker.data.services.support.IdGenerator

class CashAccountService(
    private val cashAccounts: MutableMap<CashAccountID,CashAccount>,
    idGenerator: IdGenerator
): CashAccountActions,
AccountService<CashAccount>(cashAccounts,idGenerator)