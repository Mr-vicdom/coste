package com.expensetracker.domain.concretes.account

import com.expensetracker.core.actions.BankAccountActions
import com.expensetracker.core.actions.CashAccountActions
import com.expensetracker.core.actions.CreditCardActions
import com.expensetracker.core.actions.DebitCardActions
import com.expensetracker.core.models.Account
import com.expensetracker.core.models.AccountID
import com.expensetracker.core.models.BankAccount
import com.expensetracker.core.models.CashAccount
import com.expensetracker.core.models.CreditCard
import com.expensetracker.core.models.DebitCard
import com.expensetracker.domain.contracts.account.AccountProvider

class AccountProviderImp(
    private val bankAccountActions: BankAccountActions,
    private val cashAccountActions: CashAccountActions,
    private val creditCardActions: CreditCardActions,
    private val debitCardActions: DebitCardActions,
): AccountProvider {
    override val accounts: List<Account>
        get() = mutableListOf<Account>().also {
            it += bankAccounts
            it += cashAccounts
            it += creditCards
            it += debitCards
        }

    override val bankAccounts: List<BankAccount>
        get() = bankAccountActions.getAllAccounts()

    override val cashAccounts: List<CashAccount>
        get() = cashAccountActions.getAllAccounts()

    override val creditCards: List<CreditCard>
        get() = creditCardActions.getAllAccounts()

    override val debitCards: List<DebitCard>
        get() = debitCardActions.getAllAccounts()

    override fun hasAccount(accountID: AccountID): Boolean {
        return (bankAccountActions.hasAccount(accountID) || cashAccountActions.hasAccount(accountID)
                || creditCardActions.hasAccount(accountID) || debitCardActions.hasAccount(accountID))
    }
}