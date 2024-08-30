package com.expensetracker.core.support

import com.expensetracker.core.models.CashAccount
import com.expensetracker.core.models.CreditCard
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

object Literals {
    const val DEFAULT_AMOUNT = "0"
    const val DATE = "Date"
    const val AMOUNT = "Amount"
    const val CATEGORY_ID = "CategoryID"
    const val TRANSACTION_ID = "TransactionID"
    const val ACCOUNT_ID = "AccountID"
    const val NOTE = "Note"
    const val DESCRIPTION = "Description"

    const val INCOME = "Income"
    const val EXPENSE = "Expense"
    const val TRANSFER = "Transfer"

    const val BANK_ACCOUNT = "Bank Account"
    const val CASH_ACCOUNT = "Cash Account"
    const val CREDIT_CARD = "Credit Card"
    const val DEBIT_CARD = "Debit Card"
}

object Pattern{
    const val FLOAT_REGEX_PATTERN = """[+-]?(\d*\.\d+|\d+\.\d*|\d+)"""
    const val FLOAT_TWO_DECIMALS = "%.2f"
}

object ExceptionLiterals{
    const val INVALID_AMOUNT = "Invalid Amount Entry it must by < 0.0F"
    const val INVALID_SIMPLE_NAME = "Invalid SimpleName Entry it must be < 30 in length"
    const val INVALID_TRANSACTION_DESCRIPTION = "Invalid TransactionNote Entry it must be < 1000 in length"
}

sealed class CustomException(override val message: String): Exception(message){
    data object InvalidSimpleNameException: CustomException(ExceptionLiterals.INVALID_SIMPLE_NAME)
    data object InvalidTransactionDescriptionException: CustomException(ExceptionLiterals.INVALID_TRANSACTION_DESCRIPTION)
    data object InvalidAmountException: CustomException(ExceptionLiterals.INVALID_AMOUNT)
}

object Helper {
    fun dateToMillis(localDate: LocalDate) : Long {
        return localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    fun millisToDate(long: Long): LocalDate {
        return Instant.ofEpochMilli(long).atZone(ZoneId.systemDefault()).toLocalDate()
    }
}