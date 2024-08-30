package com.expensetracker.data.services.transactions

import com.expensetracker.core.actions.ExpenseActions
import com.expensetracker.core.models.Expense
import com.expensetracker.core.models.TransactionID
import com.expensetracker.data.services.support.IdGenerator

class ExpenseService(
    private val expenses: MutableMap<TransactionID,Expense>,
    idGenerator: IdGenerator
): TransactionService<Expense>(expenses,idGenerator),
    ExpenseActions