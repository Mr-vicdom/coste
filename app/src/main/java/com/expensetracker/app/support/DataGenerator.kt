package com.expensetracker.app.support

import com.expensetracker.core.models.Account
import com.expensetracker.core.models.BankAccount
import com.expensetracker.core.models.CashAccount
import com.expensetracker.core.models.CreditCard
import com.expensetracker.core.models.DebitCard
import com.expensetracker.domain.contracts.account.AccountManager
import com.expensetracker.domain.contracts.account.AccountProvider
import com.expensetracker.domain.contracts.category.CategoryManager
import com.expensetracker.domain.contracts.category.CategoryProvider
import com.expensetracker.domain.contracts.transaction.TransactionManager
import java.time.LocalDate

object DataGenerator {

    var accountsGenerated = false
    var transactionsGenerated = false
    var categoriesGenerated = false

    fun generateDefaultAccounts(accountManager: AccountManager) {

        if(accountsGenerated) return else accountsGenerated = true
        accountManager.createBankAccount("HDFC").let { println("=>datalog $it = \"HDFC") }
        accountManager.createBankAccount("ICICI").let { println("=>datalog $it = \"ICICI") }
        accountManager.createCashAccount("MyWallet").let { println("=>datalog $it = \"MyWallet") }
        accountManager.createCreditCard("Card HDFC").let { println("=>datalog $it = \"Card HDFC") }
        accountManager.createCreditCard("UPI Card").let { println("=>datalog $it = \"UPI Card") }
        accountManager.createDebitCard("Platinum Debit", bankAccount = accountManager.bankAccounts.first()).let { println("=>datalog $it") }
        accountManager.createDebitCard("SBI Debit", bankAccount = accountManager.bankAccounts[1]).let { println("=>datalog $it") }
    }

    fun generateDefaultCategories(categoryManager: CategoryManager) {

        if(categoriesGenerated) return else categoriesGenerated = true
        categoryManager.addIncomeCategory("Salary").let { println("=>datalog $it s") }
        categoryManager.addIncomeCategory("Loan").let { println("=>datalog $it l") }
        categoryManager.addIncomeCategory("Investment").let { println("=>datalog $it i") }

        categoryManager.addExpenseCategory("Food").let { println("=>datalog $it f") }
        categoryManager.addExpenseCategory("Clothes").let { println("=>datalog $it c") }
        categoryManager.addExpenseCategory("LifeStyle").let { println("=>datalog $it") }
        categoryManager.addExpenseCategory("Travel").let { println("=>datalog $it") }
        categoryManager.addExpenseCategory("Bills").let { println("=>datalog $it") }
    }

    fun displayAll(categoryManager: CategoryManager, accountManager: AccountManager){
        categoryManager.incomeCategories.forEach {
            println("=>datalog $it")
        }
        accountManager.accounts.forEach {
            println("=>datalog $it")
        }
    }

    fun generateDummyTransactions(transactionManager: TransactionManager, categoryProvider: CategoryProvider, accountProvider: AccountProvider) {
        val bankAccount = accountProvider.bankAccounts.first()
        val creditCard = accountProvider.creditCards.first()

        val incomeNotes = listOf(
            "Monthly Salary 💼", "Freelance Payment 💻", "Bonus 🏆",
            "Dividend Income 📈", "Gift from Parents 🎁"
        )

        val expenseNotes = listOf(
            "Lunch 🍽️", "Taxi Ride 🚕", "Groceries 🛒",
            "Internet Bill 💻", "Gym Membership 💪",
            "Dinner Out 🍲", "Coffee ☕", "Clothes Shopping 👗",
            "Snacks 🍪", "Mobile Recharge 📱", "Utilities 💡"
        )

        val travelCoffeeFoodNotes = listOf(
            "Taxi Ride 🚕", "Lunch 🍽️", "Coffee ☕", "Snacks 🍪"
        )

        val transferNotes = listOf(
            "Savings Transfer 💼", "Investment 💹",
            "Emergency Fund 💸", "Loan Repayment 💵",
            "Credit Card Payment 💳"
        )
        var startDate = LocalDate.of(2024, 7, 1)
        val endDate = LocalDate.now()
        var dayCounter = 0
        var totalIncomeThisMonth = 0
        var totalExpensesThisMonth = 0

        while (!startDate.isAfter(endDate)) {
            dayCounter++

            // Generate income transactions on the first day of the month (salary) with BankAccount
            if (startDate.dayOfMonth == 1) {
                val incomeAmount = 1000
                val account = bankAccount

                transactionManager.addIncome(
                    _date = startDate,
                    _amount = incomeAmount.toString(),
                    _note = incomeNotes[0],
                    _description = "Salary for the month of ${startDate.month}",
                    category = categoryProvider.incomeCategories.random(),
                    account = account
                )
            }

            startDate = startDate.plusDays(1)
        }
    }

}