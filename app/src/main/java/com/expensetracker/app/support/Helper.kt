package com.expensetracker.app.support

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Helper {
    fun dateToString(day: Int, month: Int, year: Int): String {
        return "$day/$month/$year"
    }
    fun dateToString(date: LocalDate): String {
        return "${date.dayOfMonth}/${date.monthValue}/${date.year}"
    }
    fun stringToDate(string: String): LocalDate {
        return LocalDate.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }
}