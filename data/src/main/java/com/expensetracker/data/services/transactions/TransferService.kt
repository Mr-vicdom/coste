package com.expensetracker.data.services.transactions

import com.expensetracker.core.actions.TransferActions
import com.expensetracker.core.models.Transfer
import com.expensetracker.core.models.TransactionID
import com.expensetracker.data.services.support.IdGenerator

class TransferService(
    private val transfers: MutableMap<TransactionID,Transfer>,
    idGenerator: IdGenerator
): TransactionService<Transfer>(transfers,idGenerator),
    TransferActions