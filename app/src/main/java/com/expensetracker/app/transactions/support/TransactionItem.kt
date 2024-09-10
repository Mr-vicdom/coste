package com.expensetracker.app.transactions.support

import com.expensetracker.core.models.Transaction
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.Year

typealias WeekNumber = Int

sealed class TransactionItems {
    data class PeriodicItem(val periodicData: PeriodicDataByDay): TransactionItems()
    data class TransactionItem(val transaction: Transaction): TransactionItems()
}

sealed class TransactionItemsByWeek {
    data class PeriodicItem(val periodicData: PeriodicDataByWeek): TransactionItemsByWeek()
    data class TransactionItem(val transactionOnDay: PeriodicDataByDay): TransactionItemsByWeek()
}

sealed class TransactionItemsByMonth {
    data class PeriodicItem(val periodicData: PeriodicDataByMonth): TransactionItemsByMonth()
    data class TransactionItem(val transactionOnWeek: PeriodicDataByWeek): TransactionItemsByMonth()
}

sealed class PeriodicData(open val totalIncome: String, open val totalExpense: String)

class PeriodicDataByDay(val date: LocalDate, override val totalIncome: String, override val totalExpense: String) : PeriodicData(totalIncome, totalExpense){
    val dayOfMonth: String = date.dayOfMonth.toString()
    val dayOfWeek: DayOfWeek = date.dayOfWeek
}


class PeriodicDataByWeek(val startOfWeek: LocalDate,val endOfWeek: LocalDate, val weekNumber: WeekNumber, override val totalIncome: String, override val totalExpense: String) : PeriodicData(totalIncome, totalExpense){
    val weekStart = startOfWeek.dayOfMonth.toString().let { if (it.length == 1) "0$it" else it }.let { if(startOfWeek.dayOfMonth == 1)  "" else it }
    val weekEnd = endOfWeek.dayOfMonth.toString().let { if (it.length == 1) "0$it" else it }.let { if (startOfWeek.dayOfMonth == startOfWeek.lengthOfMonth()) "" else it }
    val total: String = try {
        (totalIncome.toDouble() - totalExpense.toDouble()).toString()
    } catch (_: Exception) {
        0.0.toString()
    }
}

class PeriodicDataByMonth(val month: Month, val year: Year,override val totalIncome: String, override val totalExpense: String) : PeriodicData(totalIncome, totalExpense) {
    val startMonth = "01"
    val endMonth = month.length(year.isLeap).toString()
    val total: String = try {
        (totalIncome.toDouble() - totalExpense.toDouble()).toString()
    } catch (_: Exception) {
        0.0.toString()
    }
}