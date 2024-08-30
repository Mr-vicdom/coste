package com.expensetracker.data.datastores

import com.expensetracker.core.models.BankAccount
import com.expensetracker.core.models.BankAccountID
import com.expensetracker.core.models.CashAccount
import com.expensetracker.core.models.CashAccountID
import com.expensetracker.core.models.Category
import com.expensetracker.core.models.CategoryID
import com.expensetracker.core.models.CreditCard
import com.expensetracker.core.models.CreditCardID
import com.expensetracker.core.models.DebitCard
import com.expensetracker.core.models.DebitCardID
import com.expensetracker.core.models.Expense
import com.expensetracker.core.models.ExpenseCategory
import com.expensetracker.core.models.Income
import com.expensetracker.core.models.IncomeCategory
import com.expensetracker.core.models.TransactionID
import com.expensetracker.core.models.Transfer
import com.expensetracker.data.datastores.support.BankDebitCards

object SimpleDataStore {
    val incomes: MutableMap<TransactionID, Income> = mutableMapOf()
    val expenses: MutableMap<TransactionID, Expense> = mutableMapOf()
    val transfers: MutableMap<TransactionID, Transfer> = mutableMapOf()

    val incomeCategories: MutableMap<CategoryID, IncomeCategory> = mutableMapOf()
    val expenseCategories: MutableMap<CategoryID, ExpenseCategory> = mutableMapOf()

    val creditCards: MutableMap<CreditCardID, CreditCard> = mutableMapOf()
    val debitCards: MutableMap<DebitCardID, DebitCard> = mutableMapOf()
    val cashAccounts: MutableMap<CashAccountID,CashAccount> = mutableMapOf()
    val bankAccounts: MutableMap<BankAccountID,BankAccount> = mutableMapOf()

    val bankDebitCards: BankDebitCards = BankDebitCards()
}