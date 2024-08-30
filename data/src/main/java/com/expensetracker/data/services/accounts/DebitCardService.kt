package com.expensetracker.data.services.accounts

import com.expensetracker.core.actions.DebitCardActions
import com.expensetracker.core.models.DebitCard
import com.expensetracker.core.models.DebitCardID
import com.expensetracker.data.services.support.IdGenerator

class DebitCardService(
    private val debitCards: MutableMap<DebitCardID,DebitCard>,
    idGenerator: IdGenerator
): DebitCardActions,
AccountService<DebitCard>(debitCards,idGenerator)