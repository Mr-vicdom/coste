package com.expensetracker.app.transactions.support

import android.icu.util.Calendar.WeekData
import com.expensetracker.core.models.Transaction
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.temporal.WeekFields
import java.util.Locale

sealed class TransactionItems {
    data class PeriodicItem(val periodicData: PeriodicDataByDay): TransactionItems()
    data class TransactionItem(val transaction: Transaction): TransactionItems()
}

sealed class TransactionItemsByWeek {
    data class PeriodicItem(val periodicData: PeriodicDataByWeek): TransactionItemsByWeek()
    data class TransactionItem(val transactionOnDay: PeriodicDataByDay): TransactionItemsByWeek()
}

sealed class PeriodicData(open val totalIncome: String, open val totalExpense: String)

class PeriodicDataByDay(val date: LocalDate, override val totalIncome: String, override val totalExpense: String) : PeriodicData(totalIncome, totalExpense){
    val dayOfMonth: String = date.dayOfMonth.toString()
    val dayOfWeek: DayOfWeek = date.dayOfWeek
}


class PeriodicDataByWeek(val startOfWeek: LocalDate,val endOfWeek: LocalDate,val weekNumber: Int, override val totalIncome: String, override val totalExpense: String) : PeriodicData(totalIncome, totalExpense){

}

class PeriodicDataByYear(val year: String, override val totalIncome: String, override val totalExpense: String) : PeriodicData(totalIncome, totalExpense)