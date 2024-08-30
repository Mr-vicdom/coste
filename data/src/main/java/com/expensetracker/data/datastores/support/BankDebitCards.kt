package com.expensetracker.data.datastores.support

import com.expensetracker.core.models.BankAccountID
import com.expensetracker.core.models.DebitCardID

class BankDebitCards {
    private val bankToCards = mutableMapOf<BankAccountID, MutableSet<DebitCardID>>()
    private val cardToBank = mutableMapOf<DebitCardID, BankAccountID>()

    fun add(bankAccountID: BankAccountID, debitCardID: DebitCardID) {
        bankToCards.computeIfAbsent(bankAccountID) { mutableSetOf() }.add(debitCardID)
        cardToBank.putIfAbsent(debitCardID,bankAccountID)
    }

    fun getCards(bankAccountID: BankAccountID): Set<DebitCardID>? {
        return bankToCards[bankAccountID]
    }

    fun getBank(debitCardID: DebitCardID): BankAccountID? {
        return cardToBank[debitCardID]
    }

    fun remove(bankAccountID: BankAccountID, debitCardID: DebitCardID): Boolean {
        bankToCards[bankAccountID]?.remove(debitCardID)
        cardToBank.remove(debitCardID)

        if (bankToCards[bankAccountID]?.isEmpty() == true) {
            bankToCards.remove(bankAccountID)
        }

        return (bankToCards[bankAccountID] == null && cardToBank[debitCardID] == null)
    }


    fun isBankHasCard(bankAccountID: BankAccountID, debitCardID: DebitCardID): Boolean {
        return bankToCards[bankAccountID]?.contains(debitCardID) == true && cardToBank.contains(debitCardID)
    }
}
