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
        val debitCard = accountProvider.debitCards.first()
        val cashAccount = accountProvider.cashAccounts.first()
        // Day 1 (20 days before today)
        transactionManager.addIncome(
            _date = LocalDate.now().minusDays(20),
            _amount = 1500.toString(),
            _note = "Salary 🤑",
            _description = "Monthly salary",
            account = bankAccount,
            category = categoryProvider.incomeCategories.random()
        )
        // bankAccount = 1500
        // debitCard = 0
        // creditCard = 0
        // cashAccount = 0

                transactionManager.addExpense(
                    _date = LocalDate.now().minusDays(20),
                    _amount = 450.toString(),
                    _note = "Online Shopping 🛍️",
                    _description = "Bought new shoes",
                    account = creditCard,
                    category = categoryProvider.expenseCategories.random()
                )
        // bankAccount = 1500
        // debitCard = 0
        // creditCard = 450
        // cashAccount = 0

        // Day 2 (19 days before today)
                transactionManager.addTransfer(
                    _date = LocalDate.now().minusDays(19),
                    _amount = 300.toString(),
                    _note = "Cash withdrawal 🏧",
                    _description = "ATM withdrawal for personal expenses",
                    fromAccount = bankAccount,
                    toAccount = cashAccount
                )
        // bankAccount = 1200
        // debitCard = 0
        // creditCard = 450
        // cashAccount = 300

                transactionManager.addExpense(
                    _date = LocalDate.now().minusDays(19),
                    _amount = 80.toString(),
                    _note = "Coffee ☕",
                    _description = "Coffee with friends",
                    account = debitCard,
                    category = categoryProvider.expenseCategories.random()
                )
        // bankAccount = 1120 (debitCard linked to bank account, debits 80)
        // debitCard = 0
        // creditCard = 450
        // cashAccount = 300

        // Day 3 (18 days before today)
                transactionManager.addExpense(
                    _date = LocalDate.now().minusDays(18),
                    _amount = 500.toString(),
                    _note = "Booking ✈️",
                    _description = "Flight booking for vacation",
                    account = creditCard,
                    category = categoryProvider.expenseCategories.random()
                )
        // bankAccount = 1120
        // debitCard = 0
        // creditCard = 950
        // cashAccount = 300

        // Day 4 (17 days before today)
                transactionManager.addExpense(
                    _date = LocalDate.now().minusDays(17),
                    _amount = 100.toString(),
                    _note = "Food 🍲",
                    _description = "Dinner at a restaurant",
                    account = creditCard,
                    category = categoryProvider.expenseCategories.random()
                )
        // bankAccount = 1120
        // debitCard = 0
        // creditCard = 1050
        // cashAccount = 300

        // Day 5 (16 days before today)
                transactionManager.addTransfer(
                    _date = LocalDate.now().minusDays(16),
                    _amount = 1050.toString(),
                    _note = "Credit Card Payment 💳",
                    _description = "Paid credit card bill",
                    fromAccount = bankAccount,
                    toAccount = creditCard
                )
        // bankAccount = 70
        // debitCard = 0
        // creditCard = 0
        // cashAccount = 300

        // Day 6 (15 days before today)
        transactionManager.addIncome(
            _date = LocalDate.now().minusDays(15),
            _amount = 800.toString(),
            _note = "Freelance Work 💻",
            _description = "Payment for graphic design project",
            account = bankAccount,
            category = categoryProvider.incomeCategories.random()
        )
        // bankAccount = 870
        // debitCard = 0
        // creditCard = 0
        // cashAccount = 300

                transactionManager.addExpense(
                    _date = LocalDate.now().minusDays(15),
                    _amount = 100.toString(),
                    _note = "Snacks 🍿",
                    _description = "Movie snacks with friends",
                    account = debitCard,
                    category = categoryProvider.expenseCategories.random()
                )
        // bankAccount = 870 (debitCard linked to bank account, debits 100)
        // debitCard = 0
        // creditCard = 0
        // cashAccount = 300

        // Day 7 (14 days before today)
                transactionManager.addExpense(
                    _date = LocalDate.now().minusDays(14),
                    _amount = 150.toString(),
                    _note = "Dinner 🍽️",
                    _description = "Dinner at a new restaurant",
                    account = creditCard,
                    category = categoryProvider.expenseCategories.random()
                )
        // bankAccount = 870
        // debitCard = 0
        // creditCard = 150
        // cashAccount = 300

        // Day 8 (13 days before today)
                transactionManager.addIncome(
                    _date = LocalDate.now().minusDays(13),
                    _amount = 500.toString(),
                    _note = "Bonus 🎉",
                    _description = "Received performance bonus",
                    account = bankAccount,
                    category = categoryProvider.incomeCategories.random()
                )
        // bankAccount = 1370
        // debitCard = 0
        // creditCard = 150
        // cashAccount = 300

                transactionManager.addExpense(
                    _date = LocalDate.now().minusDays(13),
                    _amount = 200.toString(),
                    _note = "Online Course 📚",
                    _description = "Payment for an online course",
                    account = creditCard,
                    category = categoryProvider.expenseCategories.random()
                )
        // bankAccount = 1370
        // debitCard = 0
        // creditCard = 350
        // cashAccount = 300

        // Day 9 (12 days before today)
                transactionManager.addExpense(
                    _date = LocalDate.now().minusDays(12),
                    _amount = 250.toString(),
                    _note = "Groceries 🥦",
                    _description = "Weekly grocery shopping",
                    account = debitCard,
                    category = categoryProvider.expenseCategories.random()
                )
        // bankAccount = 1120 (debitCard linked to bank account, debits 250)
        // debitCard = 0
        // creditCard = 350
        // cashAccount = 300

        // Day 10 (11 days before today)
                transactionManager.addIncome(
                    _date = LocalDate.now().minusDays(11),
                    _amount = 900.toString(),
                    _note = "Project Payment 💼",
                    _description = "Payment for consulting project",
                    account = bankAccount,
                    category = categoryProvider.incomeCategories.random()
                )
        // bankAccount = 2020
        // debitCard = 0
        // creditCard = 350
        // cashAccount = 300

        // Day 11 (10 days before today)
                transactionManager.addExpense(
                    _date = LocalDate.now().minusDays(10),
                    _amount = 150.toString(),
                    _note = "Takeout 🍔",
                    _description = "Dinner takeout from local restaurant",
                    account = creditCard,
                    category = categoryProvider.expenseCategories.random()
                )
        // bankAccount = 2020
        // debitCard = 0
        // creditCard = 500
        // cashAccount = 300

        // Day 12 (9 days before today)
                transactionManager.addTransfer(
                    _date = LocalDate.now().minusDays(9),
                    _amount = 500.toString(),
                    _note = "Transfer to Cash Account 💵",
                    _description = "Transfer cash for upcoming expenses",
                    fromAccount = bankAccount,
                    toAccount = cashAccount
                )
        // bankAccount = 1520
        // debitCard = 0
        // creditCard = 500
        // cashAccount = 800

        // Day 13 (8 days before today)
                transactionManager.addExpense(
                    _date = LocalDate.now().minusDays(8),
                    _amount = 100.toString(),
                    _note = "Transport 🚍",
                    _description = "Bus fare for commuting",
                    account = debitCard,
                    category = categoryProvider.expenseCategories.random()
                )
        // bankAccount = 1520 (debitCard linked to bank account, debits 100)
        // debitCard = 0
        // creditCard = 500
        // cashAccount = 800

        // Day 14 (7 days before today)
                transactionManager.addExpense(
                    _date = LocalDate.now().minusDays(7),
                    _amount = 250.toString(),
                    _note = "Restaurant Bill 🍽️",
                    _description = "Dinner with family",
                    account = creditCard,
                    category = categoryProvider.expenseCategories.random()
                )
        // bankAccount = 1520
        // debitCard = 0
        // creditCard = 750
        // cashAccount = 800

        // Day 15 (6 days before today)
                transactionManager.addIncome(
                    _date = LocalDate.now().minusDays(6),
                    _amount = 600.toString(),
                    _note = "Side Project Income 💻",
                    _description = "Payment for side project",
                    account = bankAccount,
                    category = categoryProvider.incomeCategories.random()
                )
        // bankAccount = 2120
        // debitCard = 0
        // creditCard = 750
        // cashAccount = 800

        // Day 16 (5 days before today)
                transactionManager.addTransfer(
                    _date = LocalDate.now().minusDays(5),
                    _amount = 750.toString(),
                    _note = "Credit Card Payment 💳",
                    _description = "Paid credit card bill",
                    fromAccount = bankAccount,
                    toAccount = creditCard
                )
        // bankAccount = 1370
        // debitCard = 0
        // creditCard = 0
        // cashAccount = 800

        // Day 17 (4 days before today)
                transactionManager.addIncome(
                    _date = LocalDate.now().minusDays(4),
                    _amount = 700.toString(),
                    _note = "Gift 🎁",
                    _description = "Received gift money",
                    account = bankAccount,
                    category = categoryProvider.incomeCategories.random()
                )
        // bankAccount = 2070
        // debitCard = 0
        // creditCard = 0
        // cashAccount = 800

        // Day 18 (3 days before today)
                transactionManager.addExpense(
                    _date = LocalDate.now().minusDays(3),
                    _amount = 100.toString(),
                    _note = "Book 📖",
                    _description = "Purchase a new book",
                    account = debitCard,
                    category = categoryProvider.expenseCategories.random()
                )
        // bankAccount = 2070 (debitCard linked to bank account, debits 100)
        // debitCard = 0
        // creditCard = 0
        // cashAccount = 800

        // Day 19 (2 days before today)
                transactionManager.addExpense(
                    _date = LocalDate.now().minusDays(2),
                    _amount = 300.toString(),
                    _note = "Dining Out 🍕",
                    _description = "Lunch with friends",
                    account = creditCard,
                    category = categoryProvider.expenseCategories.random()
                )
        // bankAccount = 2070
        // debitCard = 0
        // creditCard = 300
        // cashAccount = 800

        // Day 20 (1 day before today)
                transactionManager.addTransfer(
                    _date = LocalDate.now().minusDays(1),
                    _amount = 300.toString(),
                    _note = "Final Credit Card Payment 💳",
                    _description = "Paid off remaining credit card balance",
                    fromAccount = bankAccount,
                    toAccount = creditCard
                )
        // bankAccount = 1770
        // debitCard = 0
        // creditCard = 0
        // cashAccount = 800

        //CHATGPT made err in debitCard Calc debitCard linked to bank account
        //Crt balance
        // bankAccount = 1470
        // debitCard = 0
        // creditCard = 0
        // cashAccount = 800

    }

}