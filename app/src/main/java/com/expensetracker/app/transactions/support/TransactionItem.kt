package com.expensetracker.app.transactions.support

import com.expensetracker.core.models.Transaction
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

sealed class TransactionItems {
    data class PeriodicItem(val periodicData: PeriodicData): TransactionItems()
    data class TransactionItem(val transaction: Transaction): TransactionItems()
}


sealed class PeriodicData(open val totalIncome: String, open val totalExpense: String)

class PeriodicDataByDay(private val date: LocalDate, override val totalIncome: String, override val totalExpense: String) : PeriodicData(totalIncome, totalExpense){
    val dayOfMonth: String = date.dayOfMonth.toString()
    val dayOfWeek: DayOfWeek = date.dayOfWeek
}


class PeriodicDataByMonth(private val _month: Month, override val totalIncome: String, override val totalExpense: String) : PeriodicData(totalIncome, totalExpense){
    val month: String = _month.name
}

class PeriodicDataByYear(val year: String, override val totalIncome: String, override val totalExpense: String) : PeriodicData(totalIncome, totalExpense)