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
        accountManager.createBankAccount("HDFC")
        accountManager.createBankAccount("ICICI")
        accountManager.createCashAccount("MyWallet")
        accountManager.createCreditCard("Card HDFC")
        accountManager.createCreditCard("UPI Card")
        accountManager.createDebitCard("Platinum Debit", bankAccount = accountManager.bankAccounts.first())
        accountManager.createDebitCard("SBI Debit", bankAccount = accountManager.bankAccounts.first())
    }

    fun generateDefaultCategories(categoryManager: CategoryManager) {

        if(categoriesGenerated) return else categoriesGenerated = true
        categoryManager.addIncomeCategory("Salary")
        categoryManager.addIncomeCategory("Loan")
        categoryManager.addIncomeCategory("Investment")

        categoryManager.addExpenseCategory("Food")
        categoryManager.addExpenseCategory("Clothes")
        categoryManager.addExpenseCategory("LifeStyle")
        categoryManager.addExpenseCategory("Travel")
        categoryManager.addExpenseCategory("Bills")
    }

    fun generateDummyTransactions(transactionManager: TransactionManager, categoryProvider: CategoryProvider, accountProvider: AccountProvider) {
        // Generate 10 Income Transactions

        if(transactionsGenerated) return else transactionsGenerated = true
        transactionManager.addIncome(
            _date = LocalDate.now(),
            _amount = "100.0",
            _note = "Pocket Money",
            _description = "From mom",
            category = categoryProvider.incomeCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addIncome(
            _date = LocalDate.now().minusDays(1),
            _amount = "150.0",
            _note = "Monthly Salary",
            _description = "Monthly salary",
            category = categoryProvider.incomeCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addIncome(
            _date = LocalDate.now().minusDays(2),
            _amount = "50.0",
            _note = "Birthday Gift",
            _description = "Gift from a friend",
            category = categoryProvider.incomeCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addIncome(
            _date = LocalDate.now().minusDays(3),
            _amount = "200.0",
            _note = "Freelance Payment",
            _description = "Payment for freelance project",
            category = categoryProvider.incomeCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addIncome(
            _date = LocalDate.now().minusDays(4),
            _amount = "75.0",
            _note = "Performance Bonus",
            _description = "Bonus for good performance",
            category = categoryProvider.incomeCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addIncome(
            _date = LocalDate.now().minusDays(5),
            _amount = "80.0",
            _note = "Monthly Allowance",
            _description = "Allowance received",
            category = categoryProvider.incomeCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addIncome(
            _date = LocalDate.now().minusDays(6),
            _amount = "120.0",
            _note = "Credit Card Cashback",
            _description = "Cashback from credit card",
            category = categoryProvider.incomeCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addIncome(
            _date = LocalDate.now().minusDays(7),
            _amount = "30.0",
            _note = "Lottery Win",
            _description = "Small lottery prize",
            category = categoryProvider.incomeCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addIncome(
            _date = LocalDate.now().minusDays(8),
            _amount = "60.0",
            _note = "Sold Old Books",
            _description = "Sold used books",
            category = categoryProvider.incomeCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addIncome(
            _date = LocalDate.now().minusDays(9),
            _amount = "45.0",
            _note = "Dividend Income",
            _description = "Quarterly dividends",
            category = categoryProvider.incomeCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addExpense(
            _date = LocalDate.now().minusDays(2),
            _amount = "10.0",
            _note = "Snack",
            _description = "Bought a snack",
            category = categoryProvider.expenseCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addExpense(
            _date = LocalDate.now().minusDays(3),
            _amount = "15.0",
            _note = "Morning Coffee",
            _description = "Bought morning coffee",
            category = categoryProvider.expenseCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addExpense(
            _date = LocalDate.now().minusDays(4),
            _amount = "20.0",
            _note = "Bus Fare",
            _description = "Paid for bus fare",
            category = categoryProvider.expenseCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addExpense(
            _date = LocalDate.now().minusDays(5),
            _amount = "50.0",
            _note = "Groceries",
            _description = "Weekly grocery shopping",
            category = categoryProvider.expenseCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addExpense(
            _date = LocalDate.now().minusDays(6),
            _amount = "100.0",
            _note = "Dinner Out",
            _description = "Dinner at a restaurant",
            category = categoryProvider.expenseCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addExpense(
            _date = LocalDate.now().minusDays(7),
            _amount = "30.0",
            _note = "Cinema Ticket",
            _description = "Watched a movie",
            category = categoryProvider.expenseCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addExpense(
            _date = LocalDate.now().minusDays(8),
            _amount = "60.0",
            _note = "Electricity Bill",
            _description = "Paid electricity bill",
            category = categoryProvider.expenseCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addExpense(
            _date = LocalDate.now().minusDays(9),
            _amount = "25.0",
            _note = "Taxi Ride",
            _description = "Taxi fare",
            category = categoryProvider.expenseCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addExpense(
            _date = LocalDate.now().minusDays(10),
            _amount = "12.0",
            _note = "Evening Snacks",
            _description = "Bought snacks",
            category = categoryProvider.expenseCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addExpense(
            _date = LocalDate.now().minusDays(11),
            _amount = "40.0",
            _note = "Book Purchase",
            _description = "Bought a new book",
            category = categoryProvider.expenseCategories.random(),
            account = accountProvider.accounts.random()
        )

        transactionManager.addTransfer(
            _date = LocalDate.now().minusDays(1),
            _amount = "100.0",
            _note = "Self Transfer",
            _description = "Transferred money to another account",
            fromAccount = accountProvider.accounts.random(),
            toAccount = accountProvider.accounts.random()
        )

        transactionManager.addTransfer(
            _date = LocalDate.now().minusDays(2),
            _amount = "150.0",
            _note = "Rent Payment",
            _description = "Transferred rent to landlord",
            fromAccount = accountProvider.accounts.random(),
            toAccount = accountProvider.accounts.random()
        )

        transactionManager.addTransfer(
            _date = LocalDate.now().minusDays(3),
            _amount = "200.0",
            _note = "Savings Deposit",
            _description = "Transferred to savings account",
            fromAccount = accountProvider.accounts.random(),
            toAccount = accountProvider.accounts.random()
        )

        transactionManager.addTransfer(
            _date = LocalDate.now().minusDays(4),
            _amount = "50.0",
            _note = "Loan Repayment",
            _description = "Repayment of personal loan",
            fromAccount = accountProvider.accounts.random(),
            toAccount = accountProvider.accounts.random()
        )

        transactionManager.addTransfer(
            _date = LocalDate.now().minusDays(5),
            _amount = "300.0",
            _note = "Investment Account",
            _description = "Transferred to investment account",
            fromAccount = accountProvider.accounts.random(),
            toAccount = accountProvider.accounts.random()
        )

        transactionManager.addTransfer(
            _date = LocalDate.now().minusDays(6),
            _amount = "25.0",
            _note = "Gift Money",
            _description = "Transferred gift money to a friend",
            fromAccount = accountProvider.accounts.random(),
            toAccount = accountProvider.accounts.random()
        )

        transactionManager.addTransfer(
            _date = LocalDate.now().minusDays(7),
            _amount = "75.0",
            _note = "Charity Donation",
            _description = "Donated to charity",
            fromAccount = accountProvider.accounts.random(),
            toAccount = accountProvider.accounts.random()
        )

        transactionManager.addTransfer(
            _date = LocalDate.now().minusDays(8),
            _amount = "120.0",
            _note = "Utility Bill Payment",
            _description = "Paid utility bill",
            fromAccount = accountProvider.accounts.random(),
            toAccount = accountProvider.accounts.random()
        )

        transactionManager.addTransfer(
            _date = LocalDate.now().minusDays(9),
            _amount = "45.0",
            _note = "Emergency Fund",
            _description = "Transferred to emergency fund",
            fromAccount = accountProvider.accounts.random(),
            toAccount = accountProvider.accounts.random()
        )

        transactionManager.addTransfer(
            _date = LocalDate.now().minusDays(10),
            _amount = "90.0",
            _note = "Holiday Savings",
            _description = "Transferred to holiday savings account",
            fromAccount = accountProvider.accounts.random(),
            toAccount = accountProvider.accounts.random()
        )


    }

}