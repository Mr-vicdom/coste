package com.expensetracker.data.services.transactions

import com.expensetracker.core.actions.IncomeActions
import com.expensetracker.core.models.Income
import com.expensetracker.core.models.TransactionID
import com.expensetracker.data.services.support.IdGenerator

class IncomeService(
    private val incomes: MutableMap<TransactionID,Income>,
    idGenerator: IdGenerator
): TransactionService<Income>(incomes,idGenerator),
    IncomeActions