package com.expensetracker.app.support

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

        val incomeNotes = listOf(
            "Pocket Money 💸", "Monthly Salary 💼", "Birthday Gift 🎁",
            "Freelance Payment 💻", "Performance Bonus 🏆",
            "Monthly Allowance 💰", "Credit Card Cashback 💳",
            "Lottery Win 🎲", "Sold Old Books 📚", "Dividend Income 📈"
        )

        val expenseNotes = listOf(
            "Snack 🍪", "Morning Coffee ☕", "Bus Fare 🚌",
            "Groceries 🛒", "Dinner Out 🍽️",
            "Cinema Ticket 🎟️", "Electricity Bill 💡",
            "Taxi Ride 🚕", "Evening Snacks 🍿", "Book Purchase 📖"
        )

        val transferNotes = listOf(
            "TO Transfer 🔄", "Self ↔️", "With Draw 🏧",
            "Savings Transfer 💼", "Emergency Fund 💸",
            "Investment 💹", "Loan Repayment 💵",
            "Rent Payment 🏠", "Credit Card Payment 💳", "Gift 🎁"
        )

        for (i in 0 until 10) {
            transactionManager.addIncome(
                _date = LocalDate.now().minusDays(i.toLong()),
                _amount = (100 + i * 10).toString(),
                _note = incomeNotes[i],
                _description = "Description for ${incomeNotes[i]}",
                category = categoryProvider.incomeCategories.random(),
                account = accountProvider.accounts.random()
            )
        }

        for (i in 0 until 10) {
            transactionManager.addExpense(
                _date = LocalDate.now().minusDays(i.toLong()),
                _amount = (20 + i * 5).toString(),
                _note = expenseNotes[i],
                _description = "Description for ${expenseNotes[i]}",
                category = categoryProvider.expenseCategories.random(),
                account = accountProvider.accounts.random()
            )
        }

        val accounts = accountProvider.accounts
        for (i in 0 until 10) {
            val fromAccount = accounts.random()
            var toAccount = accounts.random()

            while (toAccount == fromAccount) {
                toAccount = accounts.random()
            }

            transactionManager.addTransfer(
                _date = LocalDate.now().minusDays(i.toLong()),
                _amount = (200 + i * 50).toString(),
                _note = transferNotes[i],
                _description = "Description for ${transferNotes[i]}",
                fromAccount = fromAccount,
                toAccount = toAccount
            )
        }


    }

}