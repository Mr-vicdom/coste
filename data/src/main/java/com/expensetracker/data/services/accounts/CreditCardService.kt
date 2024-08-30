package com.expensetracker.data.services.accounts

import com.expensetracker.core.actions.CreditCardActions
import com.expensetracker.core.models.CreditCard
import com.expensetracker.core.models.CreditCardID
import com.expensetracker.data.services.support.IdGenerator

class CreditCardService(
    private val creditCards: MutableMap<CreditCardID,CreditCard>,
    idGenerator: IdGenerator
): CreditCardActions,
AccountService<CreditCard>(creditCards,idGenerator)