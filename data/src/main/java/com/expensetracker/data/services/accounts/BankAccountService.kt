package com.expensetracker.data.services.accounts

import com.expensetracker.core.actions.BankAccountActions
import com.expensetracker.core.models.BankAccount
import com.expensetracker.core.models.BankAccountID
import com.expensetracker.data.services.support.IdGenerator

class BankAccountService(
    private val bankAccounts: MutableMap<BankAccountID,BankAccount>,
    idGenerator: IdGenerator
): BankAccountActions,
AccountService<BankAccount>(bankAccounts,idGenerator)